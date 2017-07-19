package y.multistagefold.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjie on 2017/7/19.
 */

public class Folder {

    public String id;
    // 文件名
    public String fileName;
    // 子目录文件
    public ArrayList<Folder> childFolderList;
    // 是否处于展开状态
    public boolean isExpand;



    public Folder(String id, String fileName, boolean isExpand) {
        this.id = id;
        this.fileName = fileName;
        this.isExpand = isExpand;
    }

    public ArrayList<Folder> getChildFolderList() {
        return childFolderList;
    }

    /**
     * 获取指定的文件夹
     *
     * @param positions 例如 positions = [2, 3, 1]
     *                  获取的是第1级的第2个文件夹a，再从a里获取下一级的第3个文件夹b，在从b获取下一级的第1个文件夹c，最后返回c
     *                  若positions = 0, 获取的是根目录；
     * @return
     */
    public Folder getFolder(List<Integer> positions) {
        Folder folder = this;
        for (int i = 0; i < positions.size(); i++) {
            folder = folder.getChildFolderList().get(positions.get(i));
        }
        return folder;
    }
}
