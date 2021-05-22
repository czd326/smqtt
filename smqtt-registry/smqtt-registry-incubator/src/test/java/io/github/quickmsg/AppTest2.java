package io.github.quickmsg;

import com.codahale.metrics.MetricRegistry;
import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest2
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws URISyntaxException, UnknownHostException, InterruptedException {

        GossipSettings settings = new GossipSettings();
        List<GossipMember> startupMembers = new ArrayList<>();
        URI uri2 = new URI("udp://127.0.0.1:5001" );
        startupMembers.add(new RemoteGossipMember("cluster", uri2, "1"));
        URI uri3 = new URI("udp://127.0.0.1:5003" );
        startupMembers.add(new RemoteGossipMember("cluster", uri3, "3"));
        URI uri = new URI("udp://" + "127.0.0.1:5002");
        GossipService gossipService = new GossipService("cluster", uri,   "2",null,
                startupMembers, settings, new GossipListener() {
            @Override
            public void gossipEvent(GossipMember member, GossipState state) {
                System.out.println(member+state.name());
            }
        }, new MetricRegistry());
        gossipService.start();
        SharedGossipDataMessage sendSharedGossipDataMessage= new SharedGossipDataMessage();
        sendSharedGossipDataMessage.setPayload("我來測試一下2");
        sendSharedGossipDataMessage.setKey("test2");
        sendSharedGossipDataMessage.setTimestamp(System.currentTimeMillis());
        sendSharedGossipDataMessage.setExpireAt(Long.MAX_VALUE);
        gossipService.gossipSharedData(sendSharedGossipDataMessage);
        for (;;){
            Thread.sleep(1000);
            SharedGossipDataMessage sharedGossipDataMessage1 = gossipService.findSharedData("test1");
            if(sharedGossipDataMessage1!=null){
                System.out.println("test1:"+sharedGossipDataMessage1.getPayload());
            }
            SharedGossipDataMessage sharedGossipDataMessage3 = gossipService.findSharedData("test3");
            if(sharedGossipDataMessage3!=null) {
                System.out.println("test3:"+sharedGossipDataMessage3.getPayload());
            }

        }

    }
}
