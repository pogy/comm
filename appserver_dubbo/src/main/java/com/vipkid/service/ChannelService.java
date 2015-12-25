package com.vipkid.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.vipkid.util.UrlUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Channel;
import com.vipkid.repository.ChannelRepository;
import com.vipkid.repository.UserRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.ChannelAlreadyExistServiceException;
import com.vipkid.service.pojo.Count;
import com.vipkid.util.Configurations;

@Service
public class ChannelService {

    public static final String CHANNEL_ID_KEY = "channel_id=";
    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_KEYWORD = "channel_keyword=";

    private Logger logger = LoggerFactory.getLogger(ChannelService.class.getSimpleName());

    @Resource
    private ChannelRepository channelRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private SecurityService securityService;

    public List<Channel> list(String search, Integer start, Integer length) {
        logger.info("list channel with params: search = {}, start = {}, length = {}.", search, start, length);
        return channelRepository.list(search, start, length);
    }

    public Count count(String search) {
        logger.info("count channel with params: search = {}, start = {}, length = {}.", search);
        return new Count(channelRepository.count(search));
    }

    public Channel create(Channel channel) {

        Channel channelEx = channelRepository.findChannelByName(channel.getChannelName1(), channel.getChannelName2(), channel.getChannelName3(), channel.getChannelName4(), channel.getChannelName5());
        if (channelEx != null) {
            throw new ChannelAlreadyExistServiceException("Channel already exist!");
        }
        logger.info("create order: {}", channel);
        channel.setUser(securityService.getCurrentUser());
        channel.setCreateTime(new Date());
        channel = channelRepository.create(channel);
        securityService.logAudit(Level.INFO, Category.ORDER_CREATE, "Create channel: " + channel.getSourceName());
        return channel;

    }
    public Channel generateDefaultChannel() {
        Channel channel = new Channel();
        channel.setSourceName(Configurations.Channel.WWW_DEFAULT_CHANNEL);
        channel.setChannelName1(Configurations.Channel.WWW_DEFAULT_CHANNEL_NAME1);
        channel.setChannelName2(Configurations.Channel.WWW_DEFAULT_CHANNEL_NAME2);
        channel.setChannelName3(Configurations.Channel.WWW_DEFAULT_CHANNEL_NAME3);
        channel.setChannelName4(Configurations.Channel.WWW_DEFAULT_CHANNEL_NAME4);
        channel.setChannelName5(Configurations.Channel.WWW_DEFAULT_CHANNEL_NAME5);
        channel.setCreateTime(new Date());
        channel.setLevel("D");
        channel.setSourceOld("");
        channel.setUser(userRepository.find(2));
        channel = channelRepository.create(channel);
        return channel;
    }

    public Channel getDefaultChannel() {
        Channel channel = this.findBySourceName(Configurations.Channel.WWW_DEFAULT_CHANNEL);
        if (null == channel) {
            channel = generateDefaultChannel();
        }
        return channel;
    }


    public Channel find(long channelId) {
        return channelRepository.findById(channelId);
    }

    private Channel getDefaultChannelByURL(String url) {
        Channel channel = null;

        if (url.indexOf("parent") > 0 && url.indexOf("browserLogin") > 0) {
            channel = this.findBySourceName(Configurations.Channel.MOBILE_DEFAULT_CHANNEL);
        } else if (url.indexOf("parent") > 0 && url.indexOf("login") > 0) {
            channel = this.findBySourceName(Configurations.Channel.WEIXIN_DEFAULT_CHANNEL);
        } else if (url.indexOf("www") > 0) {
            channel = this.findBySourceName(Configurations.Channel.WWW_DEFAULT_CHANNEL);
        }
        if (channel == null) {
            channel = this.getDefaultChannel();

        }
        return channel;
    }


	public Channel findChannelByURL(String url) {
		Channel channel;
		if (StringUtils.indexOf(url, CHANNEL_ID_KEY) > 0) {
			String query = StringUtils.substring(url, StringUtils.indexOf(url, "?") + 1);
            Map<String,String> paramMap = UrlUtil.getRequestMapfromUrl(query);
            if (MapUtils.isNotEmpty(paramMap)) {
                String channelID = paramMap.get(CHANNEL_ID);
                channel = channelRepository.findById(Long.parseLong(channelID));
                if (null == channel) {
                    logger.info("can not find channel,url={},get default channel", url);
                    return getDefaultChannelByURL(url);
                }else{
                    return channel;
                }
            }
		}
		return getDefaultChannelByURL(url);
	}

    public Channel update(Channel channelFromJson) {
        Channel channel = channelRepository.find(channelFromJson.getId());
        if (channel != null) {
            if (channelFromJson.getChannelName1() != null) {
                channel.setChannelName1(channelFromJson.getChannelName1());
            }
            if (channelFromJson.getChannelName2() != null) {
                channel.setChannelName2(channelFromJson.getChannelName2());
            }

            if (channelFromJson.getChannelName3() != null) {
                channel.setChannelName3(channelFromJson.getChannelName3());
            }
            if (channelFromJson.getChannelName4() != null) {
                channel.setChannelName4(channelFromJson.getChannelName4());
            }
            if (channelFromJson.getChannelName5() != null) {
                channel.setChannelName5(channelFromJson.getChannelName5());
            }
            if (channelFromJson.getSourceName() != null) {
                channel.setSourceName(channelFromJson.getSourceName());
            }
            if (channelFromJson.getLevel() != null) {
                channel.setLevel(channelFromJson.getLevel());
            }
            channelRepository.update(channel);
            securityService.logAudit(Level.INFO, Category.CHANNEL_INFO_UPDATE, "Update channel: "+ channel.getId());
            return channelFromJson;
        }
        return null;
    }

    public Channel findBySourceName(String name) {
        Channel channel = channelRepository.findBySourceName(name);
        return channel;
    }

    public List<String> getChannelList() {
        return channelRepository.getChannelList();
    }

    public Channel findByOldSource(String oldSourceName) {
        return channelRepository.findByOldSource(oldSourceName);
    }

    public static void main(String[] args) {
        String url = "http://beta-www.vipkid.com.cn/signup?channel_id=195&channel_keyword=c中文关键词 ";
        if (StringUtils.indexOf(url, CHANNEL_ID_KEY) > 0) {
            String query = StringUtils.substring(url, StringUtils.indexOf(url, "?") + 1);
            Map<String,String> paramMap = UrlUtil.getRequestMapfromUrl(query);
            if (MapUtils.isNotEmpty(paramMap)) {
                String channelID = paramMap.get(CHANNEL_ID);
                System.out.println(channelID);
            }
        }

    }
}
