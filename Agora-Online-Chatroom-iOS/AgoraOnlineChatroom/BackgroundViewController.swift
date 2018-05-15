//
//  BackgroundViewController.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

protocol BackgroundVCDelegate: NSObjectProtocol {
    func backgroundVCDidPickedFinish(_ vc:BackgroundViewController, image: UIImage, index: Int)
}

class BackgroundViewController: UIViewController {
    @IBOutlet weak var bgCollectionView: UICollectionView!
    
    weak var delegate: BackgroundVCDelegate?
    
    lazy var bgImagesList: [UIImage] = {() -> [UIImage] in
       return BackgroundOptions.bgImageList()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViewContentSize()
        updateCollectionViewLayout()
    }
}

private extension BackgroundViewController {
    func updateViewContentSize() {
        let width = UIScreen.main.bounds.width
        let height = UIScreen.main.bounds.height * 0.754
        preferredContentSize = CGSize(width: width, height: height)
    }
    
    func updateCollectionViewLayout() {
        let itemWidth = UIScreen.main.bounds.size.width * 0.25
        let itemHeight = itemWidth
        let margin = UIScreen.main.bounds.size.width * 0.04
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: itemWidth, height: itemHeight)
        bgCollectionView.contentInset = UIEdgeInsets(top: 0, left: margin, bottom: 0, right: margin)
        bgCollectionView.setCollectionViewLayout(layout, animated: false)
    }
}

extension BackgroundViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return bgImagesList.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "BackgroundCell", for: indexPath) as! BackgroundCell
        let image = bgImagesList[indexPath.item]
        cell.bgOptionsImageView.image = image
        return cell
    }
}

extension BackgroundViewController: UICollectionViewDelegate {
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let image = bgImagesList[indexPath.item]
        delegate?.backgroundVCDidPickedFinish(self, image: image, index: indexPath.item)
        self.dismiss(animated: true, completion: nil)
    }
}
