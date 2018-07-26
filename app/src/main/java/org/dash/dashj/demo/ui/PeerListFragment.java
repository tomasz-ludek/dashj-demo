package org.dash.dashj.demo.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import org.bitcoinj.core.Peer;
import org.dash.dashj.demo.R;
import org.dash.dashj.demo.event.PeerListRequestEvent;
import org.dash.dashj.demo.event.PeerListUpdateEvent;
import org.dash.dashj.demo.ui.adapter.PeerViewAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public final class PeerListFragment extends Fragment {

    private Context context;

    @BindView(R.id.peer_list_group)
    ViewAnimator viewGroup;

    @BindView(R.id.peer_list)
    RecyclerView recyclerView;

    private PeerViewAdapter adapter;

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_peer_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PeerViewAdapter(context);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new PeerListRequestEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onPeerListRequestEvent(PeerListUpdateEvent event) {
        if (adapter != null) {
            List<Peer> peerList = event.getPeerList();
            viewGroup.setDisplayedChild(peerList != null ? 1 : 0);
            adapter.replace(peerList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
