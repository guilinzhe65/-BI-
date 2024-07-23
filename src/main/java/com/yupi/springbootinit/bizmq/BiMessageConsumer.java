package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.api.AiManager;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.ExcelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author dio哒
 * @version 1.0
 * @date 2024/7/14 18:08
 */
@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        long biModelId = 1800896019109191682L;
        if (StringUtils.isBlank(message)){
            //如果失败，消息拒绝
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long chartId = Long.parseLong(message);
        //通过chartId查询到Chart。因为在Controller中我们已经提前将用户的输入存储到数据库中了
        Chart chart = chartService.getById(chartId);
        if (chart == null){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        //在调用AI服务之前，修改chart的任务状态为执行中
        Chart updateChart = new Chart();
        updateChart.setStatus("running");
        updateChart.setId(chart.getId());
        boolean b = chartService.updateById(updateChart);
        if (!b){
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(),"更新图表执行中状态失败");
            return;
        }
        //调用AI服务
        String result = aiManager.doChat(biModelId, buildUserInput(chart));
        //根据结果取出想要的数据，根据【【【【划分成两个部分，一个是图表的代码，一个是分析的数据
        String[] split = result.split("【【【【【");
        if (split.length < 3){
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(),"AI 生成错误");
            return;
        }

        //获取图表的代码
        String genChart = split[1].trim();
        //获取分析的结果
        String genResult = split[2].trim();

        //AI完成分析后，再次修改chart
        Chart updateChartResult = new Chart();
        updateChartResult.setStatus("succeed");
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        boolean updateResult = chartService.updateById(updateChartResult);

        if (!updateResult){
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(),"更新图表成功状态失败");
        }
        //消息确认
        channel.basicAck(deliveryTag,false);
    }

    /**
     * 根据用户的输入，构造向AI提问的语句
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart){
        String goal = chart.getGoal();
        String chartType = chart.getCharType();
        String csvData = chart.getChartData();
        //拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal += "请使用" + chartType;
        }
        //用户输入
        StringBuilder userInPut = new StringBuilder();
        userInPut.append("分析需求：").append("\n");
        userInPut.append(userGoal).append("\n");
        userInPut.append("原始数据：").append("\n");
        userInPut.append(csvData).append("\n");
        return userInPut.toString();
    }

    private void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }

    }
}
