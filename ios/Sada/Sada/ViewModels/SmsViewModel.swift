//
//  SmsViewModel.swift
//  Sada
//
//  Created by Nizar Mahmoud on 07/05/2025.
//

import Foundation
import Combine

private let towerPort = 8080
private let towerHealthcheck = "/healthcheck"
private let towerSend = "/send"
private let towerMessages = "/inbox"

@MainActor
class SmsViewModel: ObservableObject {
    @Published var deviceIp: String = ""
    @Published var towerIp: String = ""
    @Published var towerOnline: Bool = false
    @Published var sendError: Bool = false
    @Published var messageText: String = ""
    @Published var username: String = ""
    @Published var messages: [Message] = []

    let permissions: [String] = []
    let permissionsGranted: Bool = true

    init() {
        Task {
            self.deviceIp = NetworkUtils.getLocalIpAddress() ?? ""
            self.towerIp = NetworkUtils.getGatewayIpAddress() ?? ""
            self.towerOnline = await isTowerOnline()

            if self.towerOnline {
                await refreshMessages()
            }
        }
    }

    func updateUsername(_ text: String) {
        username = text
    }

    func updateMessageText(_ text: String) {
        if text.count <= 280 {
            messageText = text
        }
    }

    func sendMessage() {
        let message = Message(
            messageId: UUID().uuidString.lowercased(),
            username: username,
            content: messageText,
            timestamp: String(Int64(Date().timeIntervalSince1970 * 1000))
        )

        Task {
            let success = await sendMessageToServer(message: message)
            sendError = !success
            if success {
                messageText = ""
                await refreshMessages()
            }
        }
    }

    private func sendMessageToServer(message: Message) async -> Bool {
        guard !towerIp.isEmpty else { return false }
        let urlString = "http://\(towerIp):\(towerPort)\(towerSend)"
        guard let url = URL(string: urlString) else { return false }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.timeoutInterval = 10

        do {
            let data = try JSONEncoder().encode(message)
            request.httpBody = data

            let (_, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse else { return false }

            if httpResponse.statusCode == 200 {
                return true
            } else {
                print("SmsViewModel: Server returned status code:", httpResponse.statusCode)
                return false
            }
        } catch {
            print("SmsViewModel: Failed to send message:", error)
            return false
        }
    }

    private func isTowerOnline() async -> Bool {
        guard !towerIp.isEmpty else { return false }
        let urlString = "http://\(towerIp):\(towerPort)\(towerHealthcheck)"
        guard let url = URL(string: urlString) else { return false }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.timeoutInterval = 5

        do {
            let (_, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse else { return false }
            return httpResponse.statusCode == 200
        } catch {
            print("SmsViewModel: Error checking tower online:", error)
            return false
        }
    }

    func refreshMessages() async {
        do {
            messages = try await fetchMessages()
        } catch {
            print("SmsViewModel: Error refreshing inbox:", error)
            messages = []
        }
    }

    private func fetchMessages() async throws -> [Message] {
        guard !towerIp.isEmpty else { return [] }
        let urlString = "http://\(towerIp):\(towerPort)\(towerMessages)"
        guard let url = URL(string: urlString) else { return [] }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.timeoutInterval = 10

        let (data, response) = try await URLSession.shared.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NSError(domain: "SmsViewModel", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid response"])
        }

        guard httpResponse.statusCode == 200 else {
            throw NSError(domain: "SmsViewModel", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: "Server returned status code: \(httpResponse.statusCode)"])
        }

        return try JSONDecoder().decode([Message].self, from: data)
    }
}
