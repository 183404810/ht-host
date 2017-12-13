package msc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import msc.service.AMQSenderServiceImpl;
import msc.service.ConsumerService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/activemq")
public class ActiveMQController {

    //队列名gzframe.demo
    @Resource(name="queueDestination")
    private Destination queueDestination;

    //队列消息生产者
    @Resource(name="producerService")
    private AMQSenderServiceImpl producer;
    
  //队列消息消费者
    @Resource(name="consumerService")
    private ConsumerService consumer;
    
    @RequestMapping(value="/onsend",method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> producer(@RequestParam("message") String message) {
    	Map<String,Object> result=new HashMap<>();
        System.out.println("------------send to jms");
        producer.sendMessage(queueDestination, message);
        result.put("SendMessage",message);
        return result;
    }
    
    @RequestMapping(value="/receive",method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> queue_receive() throws JMSException {
        System.out.println("------------receive message");
    	Map<String,Object> result=new HashMap<>();
        
        TextMessage tm = consumer.receive(queueDestination);
        result.put("reciveMessage", tm.getText());
        return result;
    }
}
