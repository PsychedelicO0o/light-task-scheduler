package com.lts.job.task.tracker.processor;

import com.lts.job.common.protocol.JobProtos;
import com.lts.job.common.remoting.RemotingClientDelegate;
import com.lts.job.common.support.Application;
import com.lts.job.remoting.exception.RemotingCommandException;
import com.lts.job.remoting.netty.NettyRequestProcessor;
import com.lts.job.remoting.protocol.RemotingCommand;
import com.lts.job.remoting.protocol.RemotingProtos;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import static com.lts.job.common.protocol.JobProtos.RequestCode.*;

/**
 * @author Robert HG (254963746@qq.com) on 8/14/14.
 * task tracker 总的处理器, 每一种命令对应不同的处理器
 */
public class RemotingDispatcher extends AbstractProcessor {

    private static final Map<JobProtos.RequestCode, NettyRequestProcessor> processors = new HashMap<JobProtos.RequestCode, NettyRequestProcessor>();

    public RemotingDispatcher(RemotingClientDelegate remotingClient) {
        super(remotingClient);
        processors.put(PUSH_JOB, new JobPushProcessor(remotingClient));
        processors.put(JOB_ASK, new JobAskProcessor(remotingClient));
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingCommandException {

        JobProtos.RequestCode code = valueOf(request.getCode());
        NettyRequestProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.REQUEST_CODE_NOT_SUPPORTED.code(), "request code not supported!");
        }
        return processor.processRequest(ctx, request);
    }

}
