package y.multistagefold.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import y.multistagefold.R;
import y.multistagefold.adapter.MultistageFoldAdapter;
import y.multistagefold.model.Folder;

public class MainActivity extends AppCompatActivity implements MultistageFoldAdapter.IGroupClickListener{

    private ExpandableListView mExpandableListView;
    private MultistageFoldAdapter adapter;
    private Folder folderTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据
        folderTree = new Folder("0", "根目录", false);
        Folder folder1 = new Folder("1", "文档文件", false);
        Folder folder2 = new Folder("2", "视频文件", false);
        Folder folder3 = new Folder("3", "图片文件", false);
        ArrayList<Folder> list = new ArrayList<>();
        list.add(folder1);
        list.add(folder2);
        list.add(folder3);
        folderTree.childFolderList = list;


        // 初始化控件
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_lv);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                folderTree.isExpand = true;
            }
        });
        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                folderTree.isExpand = false;
            }
        });
        adapter = new MultistageFoldAdapter(this, folderTree, new ArrayList<Integer>(), 0, this);
        mExpandableListView.setAdapter(adapter);
        mExpandableListView.expandGroup(0);
    }

    // 请求数据, 假设需要利用id获取数据
    @Override
    public void onGroupClickListener(String id, List<Integer> positions) {
     // 模拟请求数据
        request(id, positions);
    }

    // 已有数据，只更新视图
    @Override
    public void onGroupClickListener() {
        notifyExpandableListDataChange();
    }


    private void request(String id, List<Integer> positions) {
        // 请求过程省略
        // 。。。。



        // 请求成功回调, 模拟数据
        ArrayList<Folder> list = new ArrayList<>();
        Random random = new Random();
        int childFolderId = random.nextInt();
        Folder folder1 = new Folder(childFolderId+"", "文件1", false);
        Folder folder2 = new Folder(childFolderId+"", "文件2", false);
        Folder folder3 = new Folder(childFolderId+"", "文件3", false);

        list.add(folder1);
        list.add(folder2);
        list.add(folder3);

        // 填充数据
        folderTree.getFolder(positions).childFolderList = list;
        notifyExpandableListDataChange();

    }

    /**
     * 数据更新
     */
    private void notifyExpandableListDataChange() {
        mExpandableListView.collapseGroup(0);
        mExpandableListView.expandGroup(0);
    }
}
