//
//  BackgroundOptions.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

struct BackgroundOptions {
    static func bgImageList() -> [UIImage] {
        var array = [UIImage]()
        for index in 0...11 {
            let imageName = "bg\(index)"
            let image = UIImage.init(named: imageName)
            array.append(image!)
        }
        return array
    }
}
