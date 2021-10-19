//
//  EMServer.swift
//  EaseMobVoice
//
//  Created by gix on 2021/8/30.
//  Copyright © 2021 EaseMob. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON
import HyphenateChat

class EMServer {
    
    private let base = "https://a1.easecdn.com/\(Keys.orgName)/\(Keys.appName)"
    
    static let shared = EMServer()
    
    private var token: String?
    
    private init() {
        // 获取到 Token 方便后期做一些聊天室的操作
        // 正式的App这些操作应该都放到Server去，不应该在App处理
        request("/token", method: .post, parameters: ["grant_type": "client_credentials", "client_id": Keys.clientID, "client_secret": Keys.clientSecret], encoding: JSONEncoding.default).responseJSON { [weak self] dp in
            let result = dp.result.map { JSON($0) }
            switch result {
                case .success(let json):
                    self?.token = json["access_token"].string
                case .failure(let error):
                    debugPrint("请检查 Keys.ClientID && Keys.ClientSecret \(error)")
            }
        }
    }
    
    func debug() {
        debugPrint("EMServer 模拟服务端操作。 正式上线前请确认这部分逻辑已经迁移至服务端")
    }
    
    func createChatRoom(_ rtcRoomId: String) {
        let semaphore = DispatchSemaphore(value: 0)
        request("/chatrooms", method: .post, parameters: ["name": rtcRoomId, "owner": EMClient.shared().currentUsername!, "description": "语聊房\(rtcRoomId) 自动生成聊天室"], encoding: JSONEncoding.default).responseJSON(queue: DispatchQueue.global()) { dp in
            let result = dp.result.map { JSON($0) }
            switch result {
                case .success(_):
                    debugPrint("生成 \(rtcRoomId) 聊天室成功")
                case .failure(let err):
                    debugPrint("create chatrooms failed. \(err)")
            }
            semaphore.signal()
        }
        semaphore.wait()
    }
    
    func fetchChatRoom(_ rtcRoomId: String, finished: @escaping (String) -> Void) -> Void {
        request("/chatrooms", method: .get).responseJSON(queue: DispatchQueue.global()) { dp in
            let result = dp.result.map { JSON($0) }
            switch result {
                case .success(let j):
                    let f = j["data"].arrayValue.first { r in
                        r["name"].stringValue == rtcRoomId
                    }
                    finished(f?["id"].string ?? "no id")
                case .failure(let err):
                    debugPrint("create chatrooms failed. \(err)")
            }
        }
    }
    
    
    @discardableResult
    open func request(
        _ endPoint: String,
        method: HTTPMethod = .get,
        parameters: Parameters? = nil,
        encoding: ParameterEncoding = URLEncoding.default,
        headers: HTTPHeaders? = nil)
        -> DataRequest
    {
        let url = URL(string: "\(base)\(endPoint)")!
        
        var httpHeaders = headers ?? [:]
        if let t = token {
            httpHeaders["Authorization"] = "Bearer \(t)"
        }

        return SessionManager.default.request(url, method: method, parameters: parameters, encoding: encoding, headers: httpHeaders)
    }

}


