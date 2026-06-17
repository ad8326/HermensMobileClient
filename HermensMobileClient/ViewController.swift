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
            await loginAndFetchStatus()
        }
    }

    private func loginAndFetchStatus() async {
        do {
            let loginResponse = try await HermensAPI.shared.login(username: "admin", password: "password")
            guard loginResponse.status == "success" else {
                await updateStatus("登录失败：\(loginResponse.message ?? "未知错误")")
                return
            }

            let statusResponse = try await HermensAPI.shared.requestStatus()
            await updateStatus("Hermens response: \(statusResponse.status) \(statusResponse.message ?? "")")
        } catch {
            await updateStatus("连接失败：\(error.localizedDescription)")
        }
    }

    @MainActor
    private func updateStatus(_ text: String) {
        statusLabel.text = text
    }
}
