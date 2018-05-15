//
//  BackMusicViewController.swift
//  AgoraOnlineChatroom
//
//  Created by CavanSu on 07/05/2018.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

protocol BackMusicVCDelegate: NSObjectProtocol {
    func backMusicVCDidSelectMusic(_ vc: BackMusicViewController, filePath: String)
    func backMusicVCDidStopMusic(_ vc:BackMusicViewController)
}

class BackMusicViewController: UIViewController {
    @IBOutlet weak var musicListTableView: UITableView!
    
    weak var delegate: BackMusicVCDelegate?
    
    lazy var musicList: [MusicFile] = {() -> [MusicFile] in
        return MusicOptions.musicList()
    }()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateSeletedMusic()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViewContentSize()
    }
}

private extension BackMusicViewController {
    func updateViewContentSize() {
        let width = UIScreen.main.bounds.width
        let height = UIScreen.main.bounds.height * 0.754
        preferredContentSize = CGSize(width: width, height: height)
    }
    
    func updateSeletedMusic() {
        if let index = MusicOptions.currentPlayingIndex, index != -1 {
            musicList[index].isPlaying = true
        }
    }
}

extension BackMusicViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return musicList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MusicCell", for: indexPath) as! MusicCell
        let musicFile = musicList[indexPath.row]
        cell.fileNameLabel.text = musicFile.fileName
        cell.isPlaying = musicFile.isPlaying
        return cell
    }
}

extension BackMusicViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // Stop
        if indexPath.row == MusicOptions.currentPlayingIndex, musicList[indexPath.row].isPlaying == true {
            delegate?.backMusicVCDidStopMusic(self)
            musicList[indexPath.row].isPlaying = false
            let lastIndex = IndexPath(row: indexPath.row, section: 0)
            tableView.reloadRows(at: [lastIndex], with: .none)
            MusicOptions.currentPlayingIndex = -1
            return
        }
        
        // Cancel Last selected
        if let index = MusicOptions.currentPlayingIndex, index != -1 {
            musicList[index].isPlaying = false
            let lastIndex = IndexPath(row: index, section: 0)
            tableView.reloadRows(at: [lastIndex], with: .none)
        }
    
        // Current selected
        MusicOptions.currentPlayingIndex = indexPath.row
        musicList[indexPath.row].isPlaying = true
        let newLastIndex = IndexPath(row: indexPath.row, section: 0)
        tableView.reloadRows(at: [newLastIndex], with: .none)
        
        delegate?.backMusicVCDidSelectMusic(self, filePath: musicList[indexPath.row].filePath)
    }
}
