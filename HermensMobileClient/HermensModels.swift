import Foundation

struct HermensStatusResponse: Decodable {
    let status: String
    let message: String?
}

struct HermensLoginRequest: Encodable {
    let username: String
    let password: String
}

struct HermensLoginResponse: Decodable {
    let status: String
    let message: String?
    let token: String?
}

struct HermensProxyRequest: Encodable {
    let path: String
    let payload: [String: AnyCodable]?
}
