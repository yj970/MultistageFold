package y.multistagefold.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import y.multistagefold.R;
import y.multistagefold.model.Folder;
import y.multistagefold.utils.DensityUtil;


public class MultistageFoldAdapter extends BaseExpandableListAdapter {
    private int level;
    private Context context;
    private IGroupClickListener groupClickListener;
    private Folder folderTree;
    // 位置
    private List<Integer> positions;
    // group高度
    private int groupViewHeight = 0;



    public MultistageFoldAdapter(Context paramContext, Folder folderTree, List<Integer> positions, int level, IGroupClickListener groupClickListener) {
        this.context = paramContext;
        this.folderTree = folderTree;
        this.level = ++level;
        this.groupClickListener = groupClickListener;
        this.positions = positions;
        groupViewHeight = DensityUtil.dp2px(paramContext, 50);
    }

    public Object getChild(int paramInt1, int paramInt2) {
        return folderTree.getFolder(positions).childFolderList;
    }

    public long getChildId(int paramInt1, int paramInt2)
    {
        return paramInt2;
    }

    public View getChildView(final int paramInt1, final int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
        // 次级目录
        final List<Integer> pos = new ArrayList<>();
        pos.addAll(positions);
        pos.add(paramInt2);

        final ChildHolder childHolder;
        if (paramView == null) {
            paramView = LayoutInflater.from(this.context).inflate(R.layout.item_child, null, false);
            childHolder = new ChildHolder();
            childHolder.lv = (ExpandableListView) paramView.findViewById(R.id.expandable_lv);
            childHolder.adapter = new MultistageFoldAdapter(context, folderTree, pos, level, groupClickListener);
            paramView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) paramView.getTag();
        }
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) childHolder.lv.getLayoutParams();

        childHolder.lv.setGroupIndicator(null);
        childHolder.lv.setAdapter(childHolder.adapter);
        // 点击事件
        childHolder.lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                folderTree.getFolder(pos).isExpand = !folderTree.getFolder(pos).isExpand;
                    if (groupClickListener != null) {
                        if (folderTree.getFolder(pos).childFolderList == null) {
                            groupClickListener.onGroupClickListener(folderTree.getFolder(pos).id, pos);
                        } else {
                            groupClickListener.onGroupClickListener();
                        }
                    }
                return false;
            }
        });

        childHolder.lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                folderTree.getFolder(pos).isExpand = true;
            }
        });

        childHolder.lv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                folderTree.getFolder(pos).isExpand = false;
            }
        });

        // 展开
        if (folderTree.getFolder(pos).isExpand) {
            childHolder.lv.expandGroup(0);
        } else {
            childHolder.lv.collapseGroup(0);
        }

        // 调整高度
        setParamsOnInit(pos, childHolder, params);

        childHolder.lv.setTag(folderTree.getFolder(pos).fileName);

        return paramView;
    }

    /**
     * 初始化时调用
     * @param pos
     * @param childHolder
     * @param params
     */
    private void setParamsOnInit(List<Integer> pos, ChildHolder childHolder, RelativeLayout.LayoutParams params) {
        // 调整高度
        ArrayList<Folder> list = folderTree.getFolder(pos).getChildFolderList();
        if (list != null && childHolder.lv.isGroupExpanded(0)) {
            // 递归
            int extra = d(folderTree.getFolder(pos), 0);
            params.height = ((extra + 1) * groupViewHeight);
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        childHolder.lv.setLayoutParams(params);
    }

    /**
     * 递归：找出该文件夹下所有文件夹的数量
     * @param Folder
     */
    private int d(Folder Folder, int n){
        if (Folder.childFolderList == null) {
            return n;
        }

        n += Folder.childFolderList.size();

        for (Folder folder : Folder.childFolderList) {
            if (folder.isExpand) {
                n = d(folder, n);
            }
        }
        return n;
    }

    public int getChildrenCount(int paramInt) {
        int count = 0;
        if (folderTree.getFolder(positions).childFolderList != null) {
            count = folderTree.getFolder(positions).childFolderList.size();
        }
        return count;
    }

    public Object getGroup(int paramInt)
    {
        return folderTree.getFolder(positions);
    }

    public int getGroupCount()
    {
        return 1;
    }

    public long getGroupId(int paramInt)
    {
        return paramInt;
    }

    public View getGroupView(int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
        GroupHolder groupHolder;
        if (paramView == null) {
            paramView = LayoutInflater.from(this.context).inflate(R.layout.item_group, null, false);
            groupHolder = new GroupHolder();
            groupHolder.textView = (TextView) paramView.findViewById(R.id.tv_file_name);
            groupHolder.parent = (LinearLayout) paramView.findViewById(R.id.parent);
            groupHolder.ivIndicator = (ImageView) paramView.findViewById(R.id.iv_indicator);
            paramView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder)paramView.getTag();
        }

        // 指示器
        if (folderTree.getFolder(positions).isExpand) {
            groupHolder.ivIndicator.setImageResource(R.mipmap.icon_expandablelistview_head_up);
        } else {
            groupHolder.ivIndicator.setImageResource(R.mipmap.icon_expandablelistview_head_down);
        }


        String groupName = folderTree.getFolder(positions).fileName;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) groupHolder.parent.getLayoutParams();
        params.height = groupViewHeight;
        groupHolder.parent.setLayoutParams(params);
        groupHolder.textView.setText(groupName);
        // 设置左边距
        params.leftMargin = (level * DensityUtil.dp2px(context, 5));
        return paramView;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public boolean isChildSelectable(int paramInt1, int paramInt2)
    {
        return true;
    }

    class GroupHolder {
        private TextView textView;
        private LinearLayout parent;
        private ImageView ivIndicator;
    }

    class ChildHolder {
        private ExpandableListView lv;
        private MultistageFoldAdapter adapter;
    }

    public interface IGroupClickListener{
        void onGroupClickListener(String id, List<Integer> positions);
        void onGroupClickListener();
    }

    public int getLevel() {
        return level;
    }

}