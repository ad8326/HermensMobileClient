import UIKit

class ViewController: UIViewController {

    @IBOutlet private weak var statusLabel: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        statusLabel.text = "Hermens mobile client ready."
    }

    @IBAction private func syncButtonTapped(_ sender: UIButton) {
        statusLabel.text = "Connecting to Hermens..."
        Task {
            await connectToHermens()
        }
    }

    private func connectToHermens() async {
        do {
            let result = try await HermensService.shared.requestStatus()
            await updateStatus("Hermens response: \(result)")
        } catch {
            await updateStatus("连接失败：\(error.localizedDescription)")
        }
    }

    @MainActor
    private func updateStatus(_ text: String) {
        statusLabel.text = text
    }
}
