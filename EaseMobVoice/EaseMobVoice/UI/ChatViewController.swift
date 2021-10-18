//
//  ChatViewController.swift
//  EaseMobLive
//
//  Created by CavanSu on 2020/3/25.
//  Copyright © 2020 EaseMob. All rights reserved.
//

import UIKit
import HyphenateChat
import MessageKit
import ZLPhotoBrowser

//class ChatCell: UITableViewCell {
////    fileprivate var fillet = FilletView(frame: CGRect.zero, filletRadius: 19.0)
//    fileprivate var tagImageView = UIImageView(frame: CGRect.zero)
//    var contentLabel = UILabel(frame: CGRect.zero)
//    var contentWidth: CGFloat = 0
//
//    var contentImage: UIImage? {
//        didSet {
//            tagImageView.image = contentImage
//        }
//    }
//
//    override func awakeFromNib() {
//        super.awakeFromNib()
//        self.transform = CGAffineTransform(scaleX: 1, y: -1)
//
//        self.backgroundColor = .clear
//        self.contentView.backgroundColor = .clear
//
////        fillet.backgroundColor = .clear
////        self.addSubview(fillet)
//
//        contentLabel.numberOfLines = 0
//        contentLabel.font = UIFont.systemFont(ofSize: 14)
//        contentLabel.backgroundColor = .clear
//        contentLabel.textColor = .white
//        self.addSubview(contentLabel)
//
//        self.tagImageView.contentMode = .scaleAspectFit
//        self.addSubview(tagImageView)
//    }
//
//    override func layoutSubviews() {
//        super.layoutSubviews()
//
//        let leftSpace: CGFloat = 15
//        let topSpace: CGFloat = 5
//
//        let labelTopSpace: CGFloat = 5 + topSpace
//
//        contentLabel.frame = CGRect(x: (leftSpace * 2),
//                                    y: labelTopSpace,
//                                    width: contentWidth,
//                                    height: self.bounds.height - (labelTopSpace * 2))
//    }
//}
//
//class ImageCell: UITableViewCell {
//
//    @IBOutlet weak var contentImageView: UIImageView?
//
//    override func awakeFromNib() {
//        self.transform = CGAffineTransform(scaleX: 1, y: -1)
//
//        self.backgroundColor = .clear
//        self.contentView.backgroundColor = .clear
//    }
//}

class ChatViewController: MessagesViewController, MessagesDataSource, MessagesLayoutDelegate, MessagesDisplayDelegate, MessageCellDelegate {
    
    var chatVM: ChatVM!
    
    private let sender = Sender(senderId: EMClient.shared().currentUsername!, displayName: Center.shared().current.info.value.name)
    
    override func viewDidLoad() {
        super.viewDidLoad()
               
        messagesCollectionView.messagesDataSource = self
        messagesCollectionView.messagesLayoutDelegate = self
        messagesCollectionView.messagesDisplayDelegate = self
        messagesCollectionView.messageCellDelegate = self

        messagesCollectionView.transform = CGAffineTransform(scaleX: 1, y: -1)
        messagesCollectionView.backgroundColor = .clear
        view.backgroundColor = .clear
        
        if let layout = messagesCollectionView.collectionViewLayout as? MessagesCollectionViewFlowLayout {
            layout.setMessageIncomingAvatarSize(.zero)
            layout.setMessageOutgoingAvatarSize(.zero)
            
            let alignment = LabelAlignment(textAlignment: .left, textInsets: UIEdgeInsets(top: 0, left: 8, bottom: 0, right: 0))
            layout.setMessageIncomingMessageTopLabelAlignment(alignment)
        }
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = super.collectionView(collectionView, cellForItemAt: indexPath)
        cell.contentView.transform = CGAffineTransform.identity.scaledBy(x: 1, y: -1)
        return cell
    }
    
    func currentSender() -> SenderType {
        return sender
    }
    
    func messageForItem(at indexPath: IndexPath, in messagesCollectionView: MessagesCollectionView) -> MessageType {
        return Message(chatVM.source[indexPath.section])
    }
    
    func numberOfSections(in messagesCollectionView: MessagesCollectionView) -> Int {
        return chatVM.source.count
    }
    
    func backgroundColor(for message: MessageType, at indexPath: IndexPath, in messagesCollectionView: MessagesCollectionView) -> UIColor {
        return UIColor(hexString: "#000000-0.3")
    }
    
    func textColor(for message: MessageType, at indexPath: IndexPath, in messagesCollectionView: MessagesCollectionView) -> UIColor {
        return .white
    }
    
    func configureMediaMessageImageView(_ imageView: UIImageView, for message: MessageType, at indexPath: IndexPath, in messagesCollectionView: MessagesCollectionView) {
        guard case MessageKind.photo(let media) = message.kind else {
            return
        }
        if media.image == nil {
            EMClient.shared().chatManager.downloadMessageThumbnail(message as? EMMessage, progress: nil) { [weak self] _, _ in
                self?.messagesCollectionView.reloadItems(at: [indexPath])
            }
        }
    }
    
    func isFromCurrentSender(message: MessageType) -> Bool {
        return false
    }
    
    func messageTopLabelAttributedText(for message: MessageType, at indexPath: IndexPath) -> NSAttributedString? {
        return NSAttributedString(string: message.sender.displayName, attributes: [.font: UIFont.systemFont(ofSize: 14.0), .foregroundColor: UIColor.white])
    }
    
    func messageTopLabelHeight(for message: MessageType, at indexPath: IndexPath, in messagesCollectionView: MessagesCollectionView) -> CGFloat {
        return message.sender.displayName != "" ? 20 : 0
    }
    
    func didTapImage(in cell: MessageCollectionViewCell) {
        if let indexPath = messagesCollectionView.indexPath(for: cell) {
            let message = chatVM.source[indexPath.section]
            let datas = chatVM.source.filter { $0.body is EMImageMessageBody }
            guard let index = datas.firstIndex(of: message) else {
                return
            }
            let vc = ZLImagePreviewController(datas: datas.compactMap { ($0.body as? EMImageMessageBody)?.remotePath.flatMap { URL(string: $0) } }, index: index, showSelectBtn: false, showBottomView: false, urlType: { _ in .image }) { url, imageView, progressFuc, finishFunc in
                guard let index = (datas.firstIndex { ($0.body as? EMImageMessageBody)?.remotePath == url.absoluteString }) else {
                    finishFunc()
                    return
                }
                let message = datas[index]
                EMClient.shared().chatManager.downloadMessageAttachment(message) { p in
                    progressFuc(CGFloat(p))
                } completion: { _, _ in
                    imageView.image = UIImage(contentsOfFile: (message.body as! EMImageMessageBody).localPath)
                    finishFunc()
                }
            }
            show(vc, sender: nil)
        }
    }
//    ZLImagePreviewController
}

struct Sender: SenderType {
    
    var senderId: String
    var displayName: String
}


struct Message: MessageType {
    
    var messageId: String {
        _m.messageId
    }
    
    private let _m: EMMessage
    
    init(_ m: EMMessage) {
        _m = m
    }
    
    public var sender: SenderType {
        let name = (_m.ext as? [String: String])?["name"]
        return Sender(senderId: _m.from, displayName: name ?? "")
    }
        
    public var sentDate: Date {
        Date(timeIntervalSince1970: Double(_m.timestamp))
    }
    
    public var kind: MessageKind {
        if let tb = _m.body as? EMTextMessageBody {
            let attr = NSMutableAttributedString(string: tb.text, attributes: [.foregroundColor: UIColor.white, .font: UIFont.systemFont(ofSize: 16)])
            Expression.enumerateEmotionParser(attr)
            return .attributedText(attr)
        }
        if let ib = _m.body as? EMImageMessageBody {
            return .photo(ImageMediaItem(body: ib))
        }
               
        fatalError("unsupport message")
    }
}

struct ImageMediaItem: MediaItem {
    
    let body: EMImageMessageBody
    
    var url: URL? {
        URL(string: body.thumbnailRemotePath)
    }
    
    var image: UIImage? {
        UIImage(contentsOfFile: body.thumbnailLocalPath)
    }
    
    var placeholderImage: UIImage {
        UIImage(named: "pic-placeholder.png")!
    }
    
    var size: CGSize {
        body.thumbnailSize
    }
    
}
