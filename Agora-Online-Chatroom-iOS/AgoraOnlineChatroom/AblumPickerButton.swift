//
//  AblumPickerButton.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class AblumPickerButton: UIButton {
    override func layoutSubviews() {
        super.layoutSubviews()
        let labelX = 20
        let labelY = (self.bounds.height - (self.titleLabel?.bounds.height)!) * 0.5
        let labelW = self.titleLabel?.bounds.width
        let labelH = self.titleLabel?.bounds.height
        self.titleLabel?.frame = CGRect(x: CGFloat(labelX), y: labelY, width: labelW!, height: labelH!)
        
        let imageViewW = 8
        let imageViewH = 11
        let imageViewX = self.bounds.width - CGFloat(imageViewW) - 20
        let imageViewY = (self.bounds.height - CGFloat(imageViewH)) * 0.5
        self.imageView?.frame = CGRect(x: imageViewX, y: imageViewY, width: CGFloat(imageViewW), height: CGFloat(imageViewH))
    }
}
