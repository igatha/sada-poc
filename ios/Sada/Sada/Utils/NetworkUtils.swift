//
//  NetworkUtils.swift
//  Sada
//
//  Created by Nizar Mahmoud on 07/05/2025.
//

import Foundation
import Network

struct NetworkUtils {
    // Returns a static local IP address.
    static func getLocalIpAddress() -> String? {
        var address: String?
        var ifaddr: UnsafeMutablePointer<ifaddrs>?

        guard getifaddrs(&ifaddr) == 0 else { return nil }
        defer { freeifaddrs(ifaddr) }

        var ptr = ifaddr
        while ptr != nil {
            defer { ptr = ptr?.pointee.ifa_next }

            guard let interface = ptr?.pointee else { continue }
            let addrFamily = interface.ifa_addr.pointee.sa_family
            if addrFamily == UInt8(AF_INET) {
                let name = String(cString: interface.ifa_name)
                if name == "en0" {
                    var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                    getnameinfo(interface.ifa_addr, socklen_t(interface.ifa_addr.pointee.sa_len),
                              &hostname, socklen_t(hostname.count),
                              nil, 0, NI_NUMERICHOST)
                    address = String(cString: hostname)
                }
            }
        }
        return address
    }

    // Returns a static gateway IP address.
    // In a real scenario, you might need to pass a context or use specific iOS APIs.
    static func getGatewayIpAddress() -> String? {
        // For now, we'll use a common gateway pattern based on the local IP
        if let localIP = getLocalIpAddress() {
            let components = localIP.split(separator: ".")
            if components.count == 4 {
                return "\(components[0]).\(components[1]).\(components[2]).5"
            }
        }
        return nil
    }
}
