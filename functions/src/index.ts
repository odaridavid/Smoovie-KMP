import { initializeApp } from "firebase-admin/app";
import { getAppCheck } from "firebase-admin/app-check";
import { defineSecret } from "firebase-functions/params";
import { onRequest } from "firebase-functions/v2/https";
import { logger } from "firebase-functions/v2";

initializeApp();

const tmdbToken = defineSecret("TMDB_TOKEN");

const TMDB_API = "https://api.themoviedb.org";

export const tmdbProxy = onRequest(
  {
    secrets: [tmdbToken],
    region: "europe-west1",
    timeoutSeconds: 30,
    memory: "256MiB",
  },
  async (request, response) => {
    const appCheckToken = request.header("X-Firebase-AppCheck");
    if (!appCheckToken) {
      response.status(401).send("Missing App Check token");
      return;
    }
    try {
      await getAppCheck().verifyToken(appCheckToken);
    } catch (error) {
      logger.warn("App Check verification failed", { error });
      response.status(401).send("Invalid App Check token");
      return;
    }

    if (request.method !== "GET") {
      response.status(405).send("Method not allowed");
      return;
    }
    if (!request.path.startsWith("/3/")) {
      response.status(404).send("Not found");
      return;
    }

    const upstream = new URL(`${TMDB_API}${request.path}`);
    for (const [key, value] of Object.entries(request.query)) {
      if (Array.isArray(value)) {
        for (const v of value) {
          if (typeof v === "string") upstream.searchParams.append(key, v);
        }
      } else if (typeof value === "string") {
        upstream.searchParams.set(key, value);
      }
    }

    let upstreamResponse: Response;
    try {
      upstreamResponse = await fetch(upstream, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${tmdbToken.value()}`,
          Accept: "application/json",
        },
      });
    } catch (error) {
      logger.error("TMDB upstream fetch failed", { path: request.path, error });
      response.status(502).send("Upstream unreachable");
      return;
    }

    if (!upstreamResponse.ok) {
      logger.warn("TMDB upstream non-2xx", {
        path: request.path,
        status: upstreamResponse.status,
      });
    }

    response.status(upstreamResponse.status);
    response.setHeader(
      "Content-Type",
      upstreamResponse.headers.get("content-type") ?? "application/json",
    );
    const body = await upstreamResponse.text();
    response.send(body);
  },
);
