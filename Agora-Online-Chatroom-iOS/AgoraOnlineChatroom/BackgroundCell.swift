//
//  BackgroundCell.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class BackgroundCell: UICollectionViewCell {
    @IBOutlet weak var bgOptionsImageView: UIImageView!
    
    override func layoutSubviews() {
        super.layoutSubviews()
        bgOptionsImageView.layer.cornerRadius = self.bounds.height * 0.5
    }
}
