//
//  NetworkUtils.swift
//  Sada
//
//  Created by Nizar Mahmoud on 07/05/2025.
//

import Foundation

struct NetworkUtils {
    // Returns a static local IP address.
    static func getLocalIpAddress() -> String? {
        return "192.168.43.134" // Placeholder IP
    }

    // Returns a static gateway IP address.
    // In a real scenario, you might need to pass a context or use specific iOS APIs.
    static func getGatewayIpAddress() -> String? {
        return "192.168.43.172" // Placeholder IP
    }
}
