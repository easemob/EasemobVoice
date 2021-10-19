//
//  ChatVM.swift
//  EaseMobLive
//
//  Created by CavanSu on 2020/3/26.
//  Copyright © 2020 EaseMob. All rights reserved.
//

import UIKit
import HyphenateChat
import MessageKit

struct Chat {
    var textSize: CGSize
    var content: NSAttributedString
    var image: UIImage?
    
    init(name: String,
         text: String,
         image: UIImage? = nil,
         widthLimit: CGFloat) {
        let tName = name
        let content = (tName + text) as NSString
        let textRect = content.boundingRect(with: CGSize(width: widthLimit, height: CGFloat(MAXFLOAT)),
                                            options: .usesLineFragmentOrigin,
                                            attributes: [NSAttributedString.Key.font : UIFont.systemFont(ofSize: 14, weight: .medium)],
                                            context: nil)
        
        let attrContent = NSMutableAttributedString(string: (content as String))
        
        attrContent.addAttributes([.foregroundColor: UIColor.white,
                                   .font: UIFont.systemFont(ofSize: 14, weight: .medium)],
                                  range: NSRange(location: 0, length: tName.count))
        
        attrContent.addAttributes([.foregroundColor: UIColor.white,
                                   .font: UIFont.systemFont(ofSize: 14)],
                                  range: NSRange(location: tName.count, length: text.utf16.count))
        
        var adjustSize = textRect.size
        adjustSize.width = adjustSize.width + 2
        self.textSize = adjustSize
        self.content = attrContent
        self.image = image
    }
}

class ChatVM: NSObject, EMChatManagerDelegate {
    
    var chatWidthLimit: CGFloat = UIScreen.main.bounds.width - 60
    
    private var conversation: EMConversation!
    
    private(set) var source: [EMMessage]
    
    weak var collectionView: MessagesCollectionView?
    
    var roomId: String! {
        didSet {
            conversation = EMClient.shared().chatManager.getConversation(roomId, type: EMConversationTypeChatRoom, createIfNotExist: true)
        }
    }
    
    override init() {
        source = []
        super.init()
        EMClient.shared().chatManager.add(self, delegateQueue: DispatchQueue.main)
        
        // 不加载历史消息
    }
    
    func messagesDidReceive(_ aMessages: [Any]!) {
        guard let messages = aMessages as? [EMMessage] else {
            return
        }
        for m in messages {
            if m.conversationId == conversation.conversationId {
                source.insert(m, at: 0)
                collectionView?.reloadData()
            }
        }
    }
       
    deinit {
        EMClient.shared().chatManager.remove(self)
    }
    
    // 这些消息均不发送到服务器
    func newMessages(_ chats: [Chat]) {
        for chat in chats {
            var body: EMMessageBody
            if let image = chat.image {
                body = EMImageMessageBody(data: image.pngData(), displayName: chat.content.string)
            } else {
                body = EMTextMessageBody(text: chat.content.string)
            }
            let message = EMMessage(conversationID: conversation.conversationId, from: EMClient.shared().currentUsername, to: conversation.conversationId, body: body, ext: nil)
            source.insert(message!, at: 0)
            collectionView?.reloadData()
        }
    }
    
    func newImages(_ images: [UIImage], name: String) {
        for image in images {
            let body = EMImageMessageBody(data: image.jpegData(compressionQuality: 1.0), thumbnailData: image.jpegData(compressionQuality: 0.5))!
            // 设置一下大小
            let scale = (UIScreen.main.bounds.size.width / 3) / max(image.size.width, image.size.height)
            body.thumbnailSize = image.size.applying(CGAffineTransform(scaleX: scale, y: scale))
            sendMessage(body)
        }
    }
    
    func newText(_ text: String, name: String) {
        sendMessage(EMTextMessageBody(text: text))
    }
    
    func sendMessage(_ body: EMMessageBody) {
        let name = Center.shared().current.info.value.name
        let message = EMMessage(conversationID: conversation.conversationId,
                                from: EMClient.shared().currentUsername,
                                to: conversation.conversationId,
                                body: body, ext: ["name": name])
        message?.chatType = EMChatTypeChatRoom
        EMClient.shared().chatManager.send(message, progress: nil, completion: nil)
        source.insert(message!, at: 0)
        collectionView?.reloadData()
    }
}
