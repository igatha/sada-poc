//
//  Message.swift
//  Sada
//
//  Created by Nizar Mahmoud on 07/05/2025.
//

import Foundation

struct Message: Codable, Identifiable {
    let messageId: String
    let username: String
    let content: String
    let timestamp: String

    var id: String { messageId }

    enum CodingKeys: String, CodingKey {
        case messageId = "message_id"
        case username
        case content
        case timestamp
    }
}
