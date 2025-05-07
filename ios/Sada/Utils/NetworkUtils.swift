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
        let monitor = NWPathMonitor(requiredInterfaceType: .wifi)
        let group = DispatchGroup()
        var gatewayIP: String?

        group.enter()

        monitor.pathUpdateHandler = { path in
            if let gateway = path.gateways.first {
                gatewayIP = gateway.debugDescription
            }
            group.leave()
        }

        let queue = DispatchQueue(label: "NetworkMonitor")
        monitor.start(queue: queue)

        // Wait for the path update with a timeout
        let result = group.wait(timeout: .now() + 2.0)
        monitor.cancel()

        if result == .success {
            if let ip = gatewayIP, ip.contains(":") {
                return ip.split(separator: ":").first.map(String.init)
            }

            return gatewayIP
        }

        // Fallback to using the local IP pattern if dynamic detection fails
        if let localIP = getLocalIpAddress() {
            let components = localIP.split(separator: ".")
            if components.count == 4 {
                return "\(components[0]).\(components[1]).\(components[2]).1"
            }
        }
        
        return nil
    }
}
