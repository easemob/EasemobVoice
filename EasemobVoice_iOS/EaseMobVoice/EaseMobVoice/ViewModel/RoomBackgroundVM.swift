//
//  RoomBackgroundVM.swift
//  EaseMobVoice
//
//  Created by CavanSu on 2020/9/16.
//  Copyright © 2020 EaseMob. All rights reserved.
//

import UIKit
import RxSwift
import RxRelay
import Armin

class RoomBackgroundVM: CustomObserver {
    var room: Room
    let selectedIndex = BehaviorRelay<Int>(value: 0)
    let selectedImage = BehaviorRelay<UIImage>(value: Center.shared().centerProvideImagesHelper().roomBackgrounds.first!)
    
    init(room: Room) {
        self.room = room
        super.init()
        observe()
    }
    
    func commit(index: Int) {
        let lastIndex = selectedIndex.value
        guard lastIndex != index else {
            return
        }
        
        selectedIndex.accept(index)
        
        let client = Center.shared().centerProvideRequestHelper()
    
        let parameters: StringAnyDic = ["backgroundImage": "\(index)"]

        let url = URLGroup.roomBackground(roomId: room.roomId)
        let event = ArRequestEvent(name: "update-room-background")
        let task = ArRequestTask(event: event,
                               type: .http(.put, url: url),
                               timeout: .low,
                               header: ["token": Keys.UserToken],
                               parameters: parameters)
        
        client.request(task: task, success: ArResponse.json({ [weak self] (_) in
            guard let strongSelf = self else {
                return
            }
            strongSelf.selectedIndex.accept(index)
        })) { [weak self] (_) -> ArRetryOptions in
            guard let strongSelf = self else {
                return .resign
            }
            strongSelf.selectedIndex.accept(lastIndex)
            strongSelf.fail.accept("update room background fail")
            return .resign
        }
    }
}

private extension RoomBackgroundVM {
    func observe() {
        message.subscribe(onNext: { [unowned self] (json) in
            guard let basic = try? json.getDictionaryValue(of: "basic") else {
                return
            }
            
            do {
                let index = try basic.getStringValue(of: "backgroundImage")
                self.selectedIndex.accept(Int(index)!)
            } catch {
                self.log(error: error)
            }
        }).disposed(by: bag)
        
        selectedIndex.map { (index) -> UIImage in
            return Center.shared().centerProvideImagesHelper().roomBackgrounds[index]
        }.bind(to: selectedImage).disposed(by: bag)
    }
}
