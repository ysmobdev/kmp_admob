import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		ComposeView().ignoresSafeArea()
	}
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        PlatformContext.shared.rootUIViewController = uiViewController
    }
}
