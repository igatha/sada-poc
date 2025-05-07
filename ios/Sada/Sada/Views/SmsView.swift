//
//  SmsView.swift
//  Sada
//
//  Created by Nizar Mahmoud on 07/05/2025.
//

import SwiftUI

struct SmsView: View {
    @StateObject var viewModel = SmsViewModel()

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 8) {
                    // Title and Description
                    Text("SMS")
                        .font(.title)
                        .bold()

                    Text("Messaging service via towers. Requires an active WiFi connection to a tower, eg. SADA-XYZ.")
                        .font(.body)
                    
                    Spacer()

                    // Connection Info
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Device IP: \(viewModel.deviceIp)")
                            .font(.caption)
                        Text("Gateway IP: \(viewModel.towerIp)")
                            .font(.caption)
                        Text("Tower Status: \(viewModel.towerOnline ? "✅" : "❌")")
                            .font(.caption)
                    }
                    
                    Spacer()

                    // Username Input
                    TextField("Enter your username...", text: $viewModel.username)
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                    // Message Input Area
                    VStack(alignment: .leading, spacing: 4) {
                        TextField("Type your message...", text: Binding(
                            get: { viewModel.messageText },
                            set: { viewModel.updateMessageText($0) }
                        ))
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                        HStack {
                            if viewModel.sendError {
                                Text("Failed to send message")
                                    .foregroundColor(.red)
                                    .font(.caption)
                            }

                            Spacer()

                            Text("\(viewModel.messageText.count)/280")
                                .font(.caption2)
                        }
                    }

                    // Send Button
                    Button(action: viewModel.sendMessage) {
                        Text("Send")
                            .frame(maxWidth: .infinity)
                            .padding(8)
                            .background(Color.accentColor)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                    
                    Spacer()

                    // Inbox Header
                    HStack {
                        Text("Inbox")
                            .font(.title3)
                            .bold()

                        Spacer()

                        Button(action: {
                            Task {
                                await viewModel.refreshMessages()
                            }
                        }) {
                            Text("Refresh")
                                .font(.caption)
                        }
                    }

                    // Messages List
                    VStack(alignment: .leading, spacing: 8) {
                        ForEach(viewModel.messages, id: \.messageId) { message in
                            VStack(alignment: .leading, spacing: 2) {
                                Text(message.username)
                                    .font(.caption)
                                Text(message.content)
                                    .font(.body)
                            }
                            .padding(.vertical, 4)
                        }
                    }
                }
                .padding()
            }
        }
    }
}

#Preview {
    SmsView()
}
