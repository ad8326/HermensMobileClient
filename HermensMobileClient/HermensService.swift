import Foundation

struct HermensResponse: Decodable {
    let status: String
    let message: String?
}

enum HermensError: Error {
    case invalidURL
    case requestFailed(Error)
    case invalidResponse
    case serverError(Int)
    case parsingFailed(Error)
}

class HermensService {
    static let shared = HermensService()
    private init() {}

    /// iOS clients should talk only to the Spring Boot backend.
    /// Change this URL to the actual backend host in deployment.
    private let baseURL = URL(string: "http://127.0.0.1:8080/api/hermens")

    func requestStatus() async throws -> String {
        guard let url = baseURL?.appendingPathComponent("status") else {
            throw HermensError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse else {
                throw HermensError.invalidResponse
            }
            guard (200...299).contains(httpResponse.statusCode) else {
                throw HermensError.serverError(httpResponse.statusCode)
            }
            let decoded = try JSONDecoder().decode(HermensResponse.self, from: data)
            return decoded.status + (decoded.message.map { ": \($0)" } ?? "")
        } catch let error as DecodingError {
            throw HermensError.parsingFailed(error)
        } catch {
            throw HermensError.requestFailed(error)
        }
    }
}
