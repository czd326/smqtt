package io.github.quickmsg.core.mqtt;

import io.github.quickmsg.common.auth.PasswordAuthentication;
import io.github.quickmsg.common.channel.ChannelRegistry;
import io.github.quickmsg.common.channel.MockMqttChannel;
import io.github.quickmsg.common.cluster.ClusterConfig;
import io.github.quickmsg.common.cluster.ClusterRegistry;
import io.github.quickmsg.common.config.AbstractConfiguration;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.MessageRegistry;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.topic.TopicRegistry;
import io.github.quickmsg.common.transport.Transport;
import io.github.quickmsg.core.DefaultChannelRegistry;
import io.github.quickmsg.core.DefaultMessageRegistry;
import io.github.quickmsg.core.DefaultProtocolAdaptor;
import io.github.quickmsg.core.DefaultTopicRegistry;
import io.github.quickmsg.core.cluster.InJvmClusterRegistry;
import lombok.Getter;
import lombok.Setter;
import reactor.netty.resources.LoopResources;

import java.util.Optional;

/**
 * @author luxurong
 */
@Getter
@Setter
public abstract class AbstractReceiveContext<T extends Configuration> implements ReceiveContext<T> {

    private T configuration;

    private LoopResources loopResources;

    private Transport<T> transport;

    private final ProtocolAdaptor protocolAdaptor;

    private final ChannelRegistry channelRegistry;

    private final TopicRegistry topicRegistry;

    private final MessageRegistry messageRegistry;

    private final PasswordAuthentication passwordAuthentication;

    private ClusterRegistry clusterRegistry;

    public AbstractReceiveContext(T configuration, Transport<T> transport) {
        this.configuration = configuration;
        this.transport = transport;
        this.protocolAdaptor = protocolAdaptor();
        this.channelRegistry = channelRegistry();
        this.topicRegistry = topicRegistry();
        this.loopResources = LoopResources.create("smqtt-cluster-io", configuration.getBossThreadSize(), configuration.getWorkThreadSize(), true);
        this.messageRegistry = messageRegistry();
        this.clusterRegistry = clusterRegistry(configuration.getClusterConfig());
        this.passwordAuthentication = basicAuthentication();
    }

    private MessageRegistry messageRegistry() {
        return Optional.ofNullable(MessageRegistry.INSTANCE)
                .orElse(new DefaultMessageRegistry());
    }

    private PasswordAuthentication basicAuthentication() {
        AbstractConfiguration abstractConfiguration = castConfiguration(configuration);
        return Optional.ofNullable(PasswordAuthentication.INSTANCE)
                .orElse(abstractConfiguration.getReactivePasswordAuth());
    }

    private ChannelRegistry channelRegistry() {
        return Optional.ofNullable(ChannelRegistry.INSTANCE)
                .orElse(new DefaultChannelRegistry());
    }

    private TopicRegistry topicRegistry() {
        return Optional.ofNullable(TopicRegistry.INSTANCE)
                .orElse(new DefaultTopicRegistry());
    }

    private ProtocolAdaptor protocolAdaptor() {
        return Optional.ofNullable(ProtocolAdaptor.INSTANCE)
                .orElse(new DefaultProtocolAdaptor())
                .proxy();
    }

    private ClusterRegistry clusterRegistry(ClusterConfig clusterConfig) {
        ClusterRegistry clusterRegistry = Optional.ofNullable(ClusterRegistry.INSTANCE)
                .orElse(new InJvmClusterRegistry());
        clusterRegistry.registry(clusterConfig);
        clusterRegistry.handlerClusterMessage()
                .subscribe(clusterMessage -> this.protocolAdaptor
                        .chooseProtocol(MockMqttChannel.
                                        DEFAULT_MOCK_CHANNEL,
                                null,
                                this));
        return clusterRegistry;
    }


    private AbstractConfiguration castConfiguration(T configuration) {
        return (AbstractConfiguration) configuration;
    }

}