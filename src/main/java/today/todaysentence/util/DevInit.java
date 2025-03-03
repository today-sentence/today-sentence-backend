 // package today.todaysentence.util;

 // import lombok.RequiredArgsConstructor;
 // import org.springframework.boot.CommandLineRunner;
 // import org.springframework.security.crypto.password.PasswordEncoder;
 // import org.springframework.stereotype.Service;
 // import today.todaysentence.domain.book.Book;
 // import today.todaysentence.domain.book.repository.BookRepository;
 // import today.todaysentence.domain.bookmark.Bookmark;
 // import today.todaysentence.domain.bookmark.repository.BookmarkRepository;
 // import today.todaysentence.domain.category.Category;
 // import today.todaysentence.domain.comment.Comment;
 // import today.todaysentence.domain.comment.repository.CommentRepository;
 // import today.todaysentence.domain.hashtag.Hashtag;
 // import today.todaysentence.domain.hashtag.service.HashtagService;
 // import today.todaysentence.domain.like.Likes;
 // import today.todaysentence.domain.like.repository.LikeRepository;
 // import today.todaysentence.domain.member.Member;
 // import today.todaysentence.domain.member.repository.MemberRepository;
 // import today.todaysentence.domain.post.Post;
 // import today.todaysentence.domain.post.repository.PostRepository;

 // import java.util.*;
 // import java.util.concurrent.ThreadLocalRandom;
 // import java.util.stream.Collectors;

 // @RequiredArgsConstructor
 // @Service
 // public class DevInit implements CommandLineRunner {

 //     private final MemberRepository memberRepository;
 //     private final BookRepository bookRepository;
 //     private final HashtagService hashtagService;
 //     private final PostRepository postRepository;
 //     private final LikeRepository likeRepository;
 //     private final BookmarkRepository bookmarkRepository;
 //     private final PasswordEncoder passwordEncoder;
 //     private final CommentRepository commentRepository;


 //     @Override
 //     public void run(String... args) throws Exception {

 //         System.out.println("\u001B[32m👤 멤버 등록 시작...\u001B[0m");

 //         List<Member> members = new ArrayList<>();

 //         for (int i = 1; i <= 10; i++) {
 //             Member member = Member
 //                     .builder()
 //                     .nickname("test" + i)
 //                     .email("test" + i + "@test.com")
 //                     .password(passwordEncoder.encode("!123456789a"))
 //                     .build();
 //             members.add(member);
 //         }
 //         memberRepository.saveAll(members);

 //         System.out.println("\u001B[32m✅ 멤버 등록 성공!\u001B[0m");

 //         System.out.println("\u001B[32m📚 도서 등록 시작...\u001B[0m");

 //         List<String> bookTitle = List.of(
 //                 "살아갈 날들을 위한 괴테의 시","채식주의자",
 //                 "소년이 온다",
 //                 "희랍어 시간",
 //                 "작별하지 않는다",
 //                 "해리포터 : 마법사의 돌",
 //                 "해리포터 : 비밀의 방",
 //                 "해리포터 : 아즈카반의 죄수",
 //                 "한강, 가족, 괴물",
 //                 "한강은 흐른다",
 //                 "모순",
 //                 "어른의 행복은 조용하다",
 //                 "내가 원하는 것을 나도 모를때",
 //                 "소스 코드: 더 비기닝"
 //                 );

 //         List<String> bookAuthor = List.of(
 //             "김종원",
 //                 "한강",
 //                 "한강",
 //                 "한강",
 //                 "한강",
 //                 "J. K. 롤링",
 //                 "J. K. 롤링",
 //                 "J. K. 롤링",
 //                 "봉준호",
 //                 "일우",
 //                 "양귀자 ",
 //                 "태수",
 //                 "전승환",
 //                 "빌 게이츠"
 //                 );
 //         List<String> bookCover = List.of(
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791199040311.jpg",//살아갈날
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9788936434595.jpg",//채식
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/200x0/pdt/9788936434120.jpg",//소년
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9788954616515.jpg",//희랍
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9788954682152.jpg",//작별
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791193790403.jpg",//해리포터:마법사의돌
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791193790663.jpg",//해리:비밀
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791193790670.jpg",//해리:아즈
 //                 "https://contents.kyobobook.co.kr/music/midi/6433/2334618.jpg",//한강,가족,괴물
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/200x0/pdt/9788926795187.jpg",//한강은 흐른다
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9788998441012.jpg",
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791169851053.jpg",
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791193937150.jpg",
 //                 "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9788932924977.jpg"
 //         );


 //         List<Book> books = new ArrayList<>();

 //         int index = 1;
 //         for (int i = 0; i < bookTitle.size(); i++) {


 //             Book book = new Book("abcd" + index, bookTitle.get(i), bookAuthor.get(i), bookCover.get(i), "출판사1", 2025);
 //             books.add(book);

 //             index++;
 //         }

 //         bookRepository.saveAll(books);

 //         System.out.println("\u001B[32m✅ 도서 등록 성공!\u001B[0m");

 //         List<String> hashtagNames = Arrays.asList(
 //                 "행복", "사랑", "책",
 //                 "여행", "음악", "영화",
 //                 "운동", "독서","감동",
 //                 "짧은명언","오늘의책",
 //                 "명언추천", "1일1독",
 //                 "책추천", "느좋","카페",
 //                 "베스트셀러", "새벽", "개발",
 //                 "시리즈"
 //         );



 //         System.out.println("\u001B[32m📝 포스트 등록 시작...\u001B[0m");

 //         List<Post> posts = new ArrayList<>();

 //         for (int i = 1; i <= 400; i++) {

 //             Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
 //             Book book = books.get(ThreadLocalRandom.current().nextInt(books.size()));
 //             int hasNumbers = ThreadLocalRandom.current().nextInt(1, 7);
 //             int categoryIndex = ThreadLocalRandom.current().nextInt(Category.values().length);

 //             List<String> hashTagsName = ThreadLocalRandom.current().ints(0, hashtagNames.size())
 //                     .distinct()
 //                     .limit(hasNumbers)
 //                     .mapToObj(hashtagNames::get)
 //                     .collect(Collectors.toList());

 //             List<Hashtag> hashtags = hashtagService.findOrCreate(hashTagsName);


 //             Post post = new Post(member,book,Category.values()[categoryIndex],hashtags,"오늘의명언 테스트 데이터 입니다"+i);



 //             postRepository.save(post);

 //             posts.add(post);
 //         }
 //         postRepository.saveAll(posts);



 //         System.out.println("\u001B[32m✅ 포스트 등록 성공!\u001B[0m");

 //         System.out.println("\u001B[32m💖 좋아요 등록 시작!\u001B[0m");

 //         List<Likes> likes = new ArrayList<>();

 //         for (int i = 1; i <= 200; i++) {
 //             Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
 //             Post post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

 //             Likes like = Likes.builder()
 //                     .memberId(member.getId())
 //                     .postId(post.getId())
 //                     .isLiked(true)
 //                     .build();

 //             likes.add(like);

 //         }
 //         likeRepository.saveAll(likes);

 //         System.out.println("\u001B[32m💖 좋아요 등록 완료!\u001B[0m");


 //         System.out.println("\u001B[32m📔 북마크 등록 시작!\u001B[0m");
 //         List<Bookmark> bookmarks =new ArrayList<>();

 //         for (int i = 1; i <= 200; i++) {
 //             Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
 //             Post post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

 //             Bookmark bookmark = new Bookmark(member,post.getId());
 //             bookmark.toggle();

 //             bookmarks.add(bookmark);

 //         }
 //         bookmarkRepository.saveAll(bookmarks);

 //         System.out.println("\u001B[32m📔 북마크  등록 완료!\u001B[0m");

 //         System.out.println("\u001B[32m📚 코멘트 등록 시작!\u001B[0m");

 //         List<Comment> comments = new ArrayList<>();

 //         for (int i = 1; i <= 300; i++) {
 //             Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
 //             Post post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

 //             Comment comment = new Comment(member,post.getId(), "테스트용 댓글 입니다"+i);
 //             comments.add(comment);

 //         }
 //         commentRepository.saveAll(comments);

 //         System.out.println("\u001B[32m📚 코멘트 등록 완료!\u001B[0m");


 //         System.out.println("\u001B[32m🎉 DevDatabaseInitializer 완료! 🎉\u001B[0m");



 //     }
 // }

