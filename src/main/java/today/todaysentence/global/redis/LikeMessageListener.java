//package today.todaysentence.global.redis;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.connection.MessageListener;
//import org.springframework.stereotype.Component;
//import today.todaysentence.domain.post.repository.PostRepository;
//import today.todaysentence.domain.post.repository.PostRepositoryCustom;
//
//@Component
//@RequiredArgsConstructor
//public class LikeMessageListener implements MessageListener {
//
//
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//
//        //전달받은 정보
//        String postId = new String(message.getBody());
//        //채널(topic) 이름
//        String chanel = new String(pattern);
//
//        System.out.println("postId "+ postId);
//        System.out.println("chanel "+ chanel);
//
//
//
//
//    }
//}
