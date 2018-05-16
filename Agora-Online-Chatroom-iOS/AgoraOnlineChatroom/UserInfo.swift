//
//  UserInfo.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 05/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

struct UserInfo {
    var uid: UInt!
    var headImage: UIImage!
    var name: String!
    
    static func fakeCurrentUser() -> UserInfo {
        var user = UserInfo()
        user.headImage = randomHeadImage()
        user.name = randomName()
        return user
    }
    
    static func fakeRemoteUser() -> UserInfo {
        var user = UserInfo()
        user.headImage = randomHeadImage()
        user.name = randomName()
        return user
    }
    
    private static func randomHeadImage() -> UIImage {
        let images = allHeadImages()
        let random = Int(arc4random() % UInt32(images.count))
        return images[random]
    }
    
    private static func allHeadImages() -> [UIImage] {
        return [#imageLiteral(resourceName: "room1"),#imageLiteral(resourceName: "room2"),#imageLiteral(resourceName: "room3"),#imageLiteral(resourceName: "room4"),#imageLiteral(resourceName: "room5")]
    }
    
    private static func randomName() -> String {
        let names = allNames()
        let random = Int(arc4random() % UInt32(names.count))
        return names[random]
    }
    
    private static func allNames() -> [String] {
        return ["Eren Jäger",
                "Mikasa Ackerman",
                "Armin Arlert",
                "Erwin Smith",
                "Rival Ackerman"]
    }
}
