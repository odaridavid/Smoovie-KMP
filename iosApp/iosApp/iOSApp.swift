import SwiftUI
import FirebaseCore
import FirebaseAppCheck
import FirebaseCrashlytics
import ComposeApp

@main
struct iOSApp: App {
    init() {
        configureFirebase()
    }

    private func configureFirebase() {
        // setAppCheckProviderFactory MUST be called before FirebaseApp.configure()
        // — Firebase locks in the factory during configure() and ignores later changes.
        AppCheck.setAppCheckProviderFactory(appCheckProviderFactory())
        FirebaseApp.configure()
        AppCheckTokenProviderRegistry.shared.instance = IosAppCheckTokenProvider()
        CrashReportingControllerRegistry.shared.instance = IosCrashReportingController()
        #if DEBUG
        if let app = FirebaseApp.app(), let provider = AppCheckDebugProvider(app: app) {
            print("===== Firebase App Check Debug Token =====")
            print(provider.currentDebugToken())
            print("==========================================")
        }
        #endif
    }

    private func appCheckProviderFactory() -> AppCheckProviderFactory {
        #if DEBUG
        return AppCheckDebugProviderFactory()
        #else
        return AppAttestProviderFactory()
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

private final class AppAttestProviderFactory: NSObject, AppCheckProviderFactory {
    func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
        AppAttestProvider(app: app)
    }
}

private final class IosAppCheckTokenProvider: NSObject, AppCheckTokenProvider {
    func fetchToken(callback: @escaping (String?) -> Void) {
        AppCheck.appCheck().token(forcingRefresh: false) { token, _ in
            callback(token?.token)
        }
    }
}

private final class IosCrashReportingController: NSObject, CrashReportingController {
    func setEnabled(enabled: Bool) {
        #if DEBUG
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
        #else
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
        #endif
    }
}
