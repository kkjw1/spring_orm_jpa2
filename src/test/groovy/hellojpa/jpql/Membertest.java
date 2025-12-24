package hellojpa.jpql;

import hellojpa.jpql.domain.Address;
import hellojpa.jpql.domain.Member;
import hellojpa.jpql.domain.MemberDTO;
import hellojpa.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLOutput;
import java.util.List;

@SpringBootTest
@Transactional
public class Membertest {
    @Autowired
    EntityManager em;

    @Test
    @Commit
    public void JPQL() throws Exception {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);

        em.flush();
        em.clear();

        // 영속성 컨텍스트에 관리 됨.
        List<MemberDTO> resultList = em.createQuery("select new hellojpa.jpql.domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();

        MemberDTO memberDTO = resultList.get(0);
        System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
        System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

//        em.createQuery()

/*        Member findMember = result.get(0);
        findMember.setAge(20);*/
    }

    @Test
    public void paging() throws Exception {
        for (int i=0; i<100; i++) {
            Member member = new Member();
            member.setUsername("member" + i);
            member.setAge(i);
            em.persist(member);
        }

        em.flush();
        em.clear();

        List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        System.out.println("resultList = " + resultList.size());
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }

    }


    @Test
    @Commit
    public void join() throws Exception {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);

        member.changeTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> result = em.createQuery("select m from Member m inner join m.team t", Member.class).getResultList();
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    @Commit
    public void caseTest() throws Exception {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);

        member.changeTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

/*        String query =
                "select " +
                        "case when m.age <= 10 then '학생요금'" +
                        "     when m.age >= 60 then '경로요금'" +
                        "     else '일반요금'" +
                        "end " +
                "from Member m";*/
        String query = "select coalesce(m.username, '이름 없는 회원') from Member m ";
        List<String> result = em.createQuery(query, String.class).getResultList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
