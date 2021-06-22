import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.boredou.user.UserApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes  = UserApplication.class)
public class RedisTest {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Test
	public void test1() {
		
		redisTemplate.opsForValue().set("name", "wangmengyuan");
		System.out.println(redisTemplate.opsForValue().get("name")); 
		
		System.out.println(111);
		
	}


}
