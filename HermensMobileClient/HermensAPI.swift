import Foundation

actor HermensAPI {
    static let shared = HermensAPI()

    private let backendBaseURL = URL(string: "http://127.0.0.1:8080/api")!
    private var authToken: String?

    func login(username: String, password: String) async throws -> HermensLoginResponse {
        let url = backendBaseURL.appendingPathComponent("auth/login")
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try JSONEncoder().encode(HermensLoginRequest(username: username, password: password))

        let (data, response) = try await URLSession.shared.data(for: request)
        try validateResponse(response)

        let decoded = try JSONDecoder().decode(HermensLoginResponse.self, from: data)
        if decoded.status == "success", let token = decoded.token {
            authToken = token
        }
        return decoded
    }

    func requestStatus() async throws -> HermensStatusResponse {
        let url = backendBaseURL.appendingPathComponent("hermens/status")
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        try addAuthHeader(to: &request)

        let (data, response) = try await URLSession.shared.data(for: request)
        try validateResponse(response)

        return try JSONDecoder().decode(HermensStatusResponse.self, from: data)
    }

    func proxy(path: String, payload: [String: AnyCodable]?) async throws -> Any {
        let url = backendBaseURL.appendingPathComponent("hermens/proxy")
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        try addAuthHeader(to: &request)
        request.httpBody = try JSONEncoder().encode(HermensProxyRequest(path: path, payload: payload))

        let (data, response) = try await URLSession.shared.data(for: request)
        try validateResponse(response)

        return try JSONSerialization.jsonObject(with: data, options: [])
    }

    func logout() {
        authToken = nil
    }

    // MARK: - Helpers

    private func addAuthHeader(to request: inout URLRequest) throws {
        guard let authToken = authToken else {
            throw HermensError.invalidResponse
        }
        request.setValue("Bearer \(authToken)", forHTTPHeaderField: "Authorization")
    }

    private func validateResponse(_ response: URLResponse) throws {
        guard let httpResponse = response as? HTTPURLResponse else {
            throw HermensError.invalidResponse
        }
        guard (200...299).contains(httpResponse.statusCode) else {
            throw HermensError.serverError(httpResponse.statusCode)
        }
    }
}
