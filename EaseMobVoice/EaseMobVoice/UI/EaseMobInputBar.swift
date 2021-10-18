//
//  EaseMobInputBar.swift
//  EaseMobVoice
//
//  Created by gix on 2021/8/31.
//  Copyright © 2021 EaseMob. All rights reserved.
//

import Foundation
import InputBarAccessoryView
import SwiftyJSON
import ZLPhotoBrowser

protocol EaseMobInputBarImageDelegate: InputBarAccessoryViewDelegate {
    
    func inputBar(_ inputBar: InputBarAccessoryView, didSelect images: [UIImage])
}

class EaseMobInputBar: InputBarAccessoryView, UITextViewDelegate {
    
    weak var imageDelegate: EaseMobInputBarImageDelegate?
    
    private lazy var emojiKeyboard = EaseMobEmojiInputView.createFromNib(self)
    private var showEmojiKeybaord = false
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override var canBecomeFirstResponder: Bool {
        showEmojiKeybaord
    }
    
    override var inputView: UIView? {
        emojiKeyboard
    }
    
    @discardableResult
    override func resignFirstResponder() -> Bool {
        showEmojiKeybaord = false
        return super.resignFirstResponder()
    }
    
    private func configure() {
        
        inputTextView.backgroundColor = .white.withAlphaComponent(0.15)
        
        let backgroundColor = UIColor(red: 0.0, green: 0.039, blue: 0.0157, alpha: 1.0)
        backgroundView.backgroundColor = backgroundColor
        var e = inputTextView.textContainerInset
        e.left = 8.0
        inputTextView.textContainerInset = e
        e = inputTextView.placeholderLabelInsets
        e.left = 16.0
        inputTextView.placeholderLabelInsets = e
        
        inputTextView.textColor = .white
        inputTextView.tintColor = .white
        inputTextView.layer.borderColor = backgroundColor.cgColor
        inputTextView.layer.borderWidth = 1.0
        inputTextView.layer.cornerRadius = 4.0
        inputTextView.layer.masksToBounds = true
        inputTextView.returnKeyType = .send
        inputTextView.delegate = self
        
        middleContentViewPadding = UIEdgeInsets(top: 4, left: 8, bottom: 4, right: 8)
                
        let voiceItem = makeButton(named: "voice")
        voiceItem.spacing = .fixed(10)
        setLeftStackViewWidthConstant(to: 30.0, animated: false)
        setStackViewItems([voiceItem], forStack: .left, animated: false)
        
        setRightStackViewWidthConstant(to: 70, animated: false)
        setStackViewItems([
            makeButton(named: "emoji"),
            makeButton(named: "add")
        ], forStack: .right, animated: false)
    }
        
    private func makeButton(named: String) -> InputBarButtonItem {
        return InputBarButtonItem().configure {
            $0.image = UIImage(named: named)?.withRenderingMode(.alwaysTemplate)
            $0.setSize(CGSize(width: 30.0, height: 30.0), animated: false)
            $0.tintColor = .lightGray
        }.onSelected {
            $0.tintColor = .white
        }.onDeselected {
            $0.tintColor = .lightGray
        }.onTouchUpInside { [weak self] _ in
            guard let `self` = self else {
                return
            }
            switch named {
                case "emoji":
                    if self.isFirstResponder {
                        self.resignFirstResponder()
                    } else {
                        self.showEmojiKeybaord = true
                        self.becomeFirstResponder()
                    }
                case "voice":
                    if let vc = self.delegate as? ChatRoomViewController {
                        vc.showTextToast(text: "正在开发中，敬请期待 ...")
                    }
                case "add":
                    guard let vc = self.delegate as? ChatRoomViewController else {
                        return
                    }
                    vc.toggleInputBar(false)
                    
                    let ps = ZLPhotoPreviewSheet()
                    ps.selectImageBlock = { [weak self] (images, assets, isOriginal) in
                        self?.imageDelegate?.inputBar(self!, didSelect: images)
                    }
                    ps.showPhotoLibrary(sender: vc)
                default:
                    break
            }
        }
    }
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if text == "\n" {
            delegate?.inputBar(self, didPressSendButtonWith: inputTextView.text)
            return false
        }
        return true
    }
}

class EaseMobEmojiInputView: UIView {
    
    @IBOutlet weak var collectionView: UICollectionView?
    @IBOutlet weak var layout: UICollectionViewFlowLayout?
    
    weak var bar: InputBarAccessoryView?
    
    private var dataSource = JSON().arrayValue
    
    private var inputEmojies = [String]()
    
    static func createFromNib(_ bar: InputBarAccessoryView) -> EaseMobEmojiInputView {
        
        let name = "EaseMobEmojiInputView"
        
        if Bundle.main.path(forResource: name, ofType: "nib") != nil {
            let v = UINib(nibName: name, bundle:nil).instantiate(withOwner: nil, options: nil)[0] as! EaseMobEmojiInputView
            v.frame = CGRect(origin: CGPoint.zero, size: CGSize(width: UIScreen.main.bounds.width, height: 300))
            v.bar = bar
            return v
        } else {
            fatalError()
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        //从plist中读取出来
        guard let emojiArray = NSArray(contentsOfFile: Expression.plist!) else {
            fatalError()
        }
        dataSource = JSON(emojiArray).arrayValue
        
        collectionView?.register(UICollectionViewEmojiCell.self, forCellWithReuseIdentifier: "cell")
        collectionView?.delegate = self
        collectionView?.dataSource = self
        collectionView?.reloadData()
        collectionView?.contentInset = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        collectionView?.backgroundColor = UIColor(red: 0.0, green: 0.039, blue: 0.0157, alpha: 1.0)
        
        layout?.itemSize = CGSize(width: 30, height: 30)
    }
    
    @IBAction func tapDel(sender: UIButton) {
        if let str = inputEmojies.popLast(), let text = bar?.inputTextView.text {
            if text.hasSuffix(str) {
                let index = text.index(text.endIndex, offsetBy: -str.count)
                bar?.inputTextView.text = String(text[..<index])
            }
        }
    }
    
    @IBAction func tapSend(sender: UIButton) {
        bar?.delegate?.inputBar(bar!, didPressSendButtonWith: bar!.inputTextView.text)
    }
}

extension EaseMobEmojiInputView: UICollectionViewDataSource {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return dataSource.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as? UICollectionViewEmojiCell else {
            fatalError()
        }
        
        let emoji = dataSource[indexPath.item]
//        let text = emoji["text"].string
        let resource = emoji["image"].stringValue
        if let path = Expression.bundle.path(forResource: "\(resource)@2x", ofType: "png") {
            let image = UIImage(contentsOfFile: path)
            cell.imageView?.image = image
        }
        
        return cell
    }
}

extension EaseMobEmojiInputView: UICollectionViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let emoji = dataSource[indexPath.item]
        let text = emoji["text"].stringValue
        inputEmojies.append(text)
        bar?.inputTextView.text = bar!.inputTextView.text + text
    }
}

class UICollectionViewEmojiCell: UICollectionViewCell {
    
    var imageView: UIImageView?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        imageView = UIImageView()
        contentView.addSubview(imageView!)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        imageView?.center = center
        imageView?.frame = bounds
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

class Expression {
    static let bundle = Bundle(url: Bundle.main.url(forResource: "Expression", withExtension: "bundle")!)!
    static let bundleName = "Expression.bundle"
    static let plist = Bundle.main.path(forResource: "Expression", ofType: "plist")
    
    static var all: [String: String] = {
        guard let emojiArray = NSArray(contentsOfFile: Expression.plist!) else {
            fatalError()
        }
        return JSON(emojiArray).arrayValue.reduce([:]) { r, e in
            var nextR = r
            nextR[e["text"].stringValue] = e["image"].stringValue
            return nextR
        }
    }()
    
    
    /**
     正则：匹配 [哈哈] [笑哭。。] 表情
     */
    class var regexEmotions: NSRegularExpression {
        get {
            let regularExpression = try! NSRegularExpression(pattern: "\\[[^\\[\\]]+?\\]", options: [.caseInsensitive])
            return regularExpression
        }
    }
    
    /**
     /匹配 [表情]
     
     - parameter attributedText: 富文本
     - parameter fontSize:       字体大小
     */
    class func enumerateEmotionParser(_ attributedText: NSMutableAttributedString) {
        let emoticonResults = regexEmotions.matches(
            in: attributedText.string,
            options: [.reportProgress],
            range: NSMakeRange(0, attributedText.length)
        )
        var emoClipLength: Int = 0
        for emotion: NSTextCheckingResult in emoticonResults {
            if emotion.range.location == NSNotFound && emotion.range.length <= 1 {
                continue
            }
            
            var range: NSRange  = emotion.range
            range.location -= emoClipLength
            
            let start = attributedText.string.index(attributedText.string.startIndex, offsetBy: range.location)
            let end = attributedText.string.index(attributedText.string.startIndex, offsetBy: range.location + range.length)
            
            let emoji = String(attributedText.string[start..<end])
            guard let imageName = Expression.all[emoji]  else {
                continue
            }
            
            if let imagePath = Expression.bundle.path(forResource: "\(imageName)@2x", ofType: "png") {
                let attachment = NSTextAttachment()
                let emoji = UIImage(contentsOfFile: imagePath)!
                attachment.image = emoji
                attachment.bounds = CGRect(x: 0, y: -6, width: emoji.size.width, height: emoji.size.height)
                let emojiText = NSAttributedString(attachment: attachment)
                attributedText.replaceCharacters(in: range, with: emojiText)
                
            }
            emoClipLength += range.length - 1
        }
    }
}
