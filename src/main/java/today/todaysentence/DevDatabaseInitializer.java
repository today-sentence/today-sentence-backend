package today.todaysentence;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.book.Book;
import today.todaysentence.domain.book.repository.BookRepository;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.service.HashtagService;
import today.todaysentence.domain.like.Likes;
import today.todaysentence.domain.like.repository.LikeRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.repository.PostRepository;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DevDatabaseInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;


    @Override
    public void run(String... args) throws Exception {

        System.out.println("\u001B[32m👤 멤버 등록 시작...\u001B[0m");

        List<Member> members = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Member member = Member
                    .builder()
                    .nickname("test" + i)
                    .email("test" + i + "@test.com")
                    .password("12345")
                    .build();
            members.add(member);
        }
        memberRepository.saveAll(members);

        System.out.println("\u001B[32m✅ 멤버 등록 성공!\u001B[0m");

        System.out.println("\u001B[32m📚 도서 등록 시작...\u001B[0m");

        Map<String, String> bookList = new HashMap<>();

        bookList.put("살아갈 날들을 위한 괴테의 시", "김종원");
        bookList.put("채식주의자", "한강");
        bookList.put("소년이 온다", "한강");
        bookList.put("희랍어 시간", "한강");
        bookList.put("해리포터 : 마법사의 돌", "J. K. 롤링");
        bookList.put("해리포터 : 비밀의 방", "J. K. 롤링");
        bookList.put("해리포터 : 아즈카반의 죄수", "J. K. 롤링");
        bookList.put("한강, 가족, 괴물", "봉준호");
        bookList.put("한강은 흐른다", "일우");
        bookList.put("김종원", "테스트");
        bookList.put("트럼프", "해리스");

        List<Book> books = new ArrayList<>();

        int index = 1;
        for (Map.Entry<String, String> entry : bookList.entrySet()) {
            String title = entry.getKey();
            String author = entry.getValue();

            Book book = new Book("abcd" + index, title, author, "image.url", "출판사1", 2025);
            books.add(book);

            index++;
        }

        bookRepository.saveAll(books);

        System.out.println("\u001B[32m✅ 도서 등록 성공!\u001B[0m");

        List<String> hashtagNames = Arrays.asList(
                "행복", "사랑", "책",
                "여행", "음악", "영화",
                "운동", "독서","감동",
                "짧은명언","오늘의책",
                "명언추천", "1일1독",
                "책추천", "느좋","카페",
                "베스트셀러", "새벽", "개발",
                "시리즈"
        );

        List<String> categories = Arrays.asList(
                "시/소설/에세이",
                "경제/경영",
                "역사/사회",
                "철학/심리학",
                "자기계발",
                "예체능",
                "아동/청소년",
                "여행/문화",
                "기타");

        System.out.println("\u001B[32m📝 포스트 등록 시작...\u001B[0m");

        List<Post> posts = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {

            Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
            Book book = books.get(ThreadLocalRandom.current().nextInt(books.size()));
            int hasNumbers = ThreadLocalRandom.current().nextInt(1, 7);
            int categoryIndex = ThreadLocalRandom.current().nextInt(Category.values().length);

            List<String> hashTagsName = ThreadLocalRandom.current().ints(0, hashtagNames.size())
                    .distinct()
                    .limit(hasNumbers)
                    .mapToObj(hashtagNames::get)
                    .collect(Collectors.toList());

            List<Hashtag> hashtags = hashtagService.findOrCreate(hashTagsName);


            Post post = new Post(member,book,Category.values()[categoryIndex],hashtags,"오늘의명언 테스트 데이터 입니다"+i);



            postRepository.save(post);

            posts.add(post);
        }
        postRepository.saveAll(posts);

        List<Likes> likes = new ArrayList<>();


        System.out.println("\u001B[32m✅ 포스트 등록 성공!\u001B[0m");

        System.out.println("\u001B[32m💖 좋아요 등록 시작!\u001B[0m");

        for (int i = 1; i <= 100; i++) {
            Member member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
            Post post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

            Likes like = Likes.builder()
                    .member(member)
                    .post(post)
                    .isLiked(true)
                    .build();

            likes.add(like);

        }
        likeRepository.saveAll(likes);

        System.out.println("\u001B[32m💖 좋아요 등록 완료!\u001B[0m");


        System.out.println("\u001B[32m🎉 DevDatabaseInitializer 완료! 🎉\u001B[0m");

    }
}

