import SwiftUI
import FirebaseCore
import FirebaseAppCheck
import ComposeApp

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        let factory: AppCheckProviderFactory
        #if DEBUG
        factory = AppCheckDebugProviderFactory()
        #else
        factory = AppAttestProviderFactory()
        #endif
        AppCheck.setAppCheckProviderFactory(factory)
        AppCheckTokenProviderRegistry.shared.instance = IosAppCheckTokenProvider()
        #if DEBUG
        if let app = FirebaseApp.app(), let provider = AppCheckDebugProvider(app: app) {
            print("===== Firebase App Check Debug Token =====")
            print(provider.currentDebugToken())
            print("==========================================")
        }
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

final class IosAppCheckTokenProvider: NSObject, AppCheckTokenProvider {
    func fetchToken(callback: @escaping (String?) -> Void) {
        AppCheck.appCheck().token(forcingRefresh: false) { token, _ in
            callback(token?.token)
        }
    }
}
