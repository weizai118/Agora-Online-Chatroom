//
//  RoomListViewController.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 04/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class RoomListViewController: UIViewController {

    @IBOutlet weak var roomsCollectionView: UICollectionView!
    
    lazy var roomsList: [RoomInfoModel] = {() -> [RoomInfoModel] in
        return RoomInfos.list()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        updateCollectionViewLayout()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier, !segueId.isEmpty else {
            return
        }
        
        switch segueId {
        case "JoinRoom":
            let inputRoomIdVC = segue.destination as! InputRoomIdViewController
            inputRoomIdVC.delegate = self
            setPopoverDelegate(of: inputRoomIdVC, sender:sender as! UIButton)
        default: break
        }
    }
}

private extension RoomListViewController {
    func updateCollectionViewLayout() {
        let itemWidth = UIScreen.main.bounds.size.width * 0.427
        let itemHeight = UIScreen.main.bounds.size.height * 0.285
        let margin = UIScreen.main.bounds.size.width * 0.04
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: itemWidth, height: itemHeight)
        roomsCollectionView.contentInset = UIEdgeInsets(top: 0, left: margin, bottom: 0, right: margin)
        roomsCollectionView.setCollectionViewLayout(layout, animated: false)
    }
    
    func setPopoverDelegate(of vc: UIViewController, sender: UIButton) {
        vc.popoverPresentationController?.delegate = self
        vc.popoverPresentationController?.sourceRect = CGRect(x: sender.bounds.size.width * 0.5, y: 0, width: 0, height: 0)
    }
}

extension RoomListViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return roomsList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "room", for: indexPath) as! RoomCell
        let roomInfo = roomsList[indexPath.item]
        cell.roomImageView.image = roomInfo.image
        cell.roomNameLabel.text = roomInfo.name
        cell.peopleCountLabel.text = "\(roomInfo.peopleCount!)人"
        return cell
    }
}

extension RoomListViewController: UIPopoverPresentationControllerDelegate {
    func adaptivePresentationStyle(for controller: UIPresentationController, traitCollection: UITraitCollection) -> UIModalPresentationStyle {
        return .none
    }
}

extension RoomListViewController: InputRoomIdVCDelegate {
    func inputRoomIdVCDidJoin(_ vc: InputRoomIdViewController, roomID: String) {
        vc.dismiss(animated: true, completion: nil)
        let storyBoard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
        let roomVC = storyBoard.instantiateViewController(withIdentifier: "RoomViewController") as! RoomViewController
        roomVC.externalRole = .audience
        roomVC.roomID = roomID
        roomVC.roomName = RoomInfos.fakeRoomName()
        self.present(roomVC, animated: true, completion: nil)
    }
}
