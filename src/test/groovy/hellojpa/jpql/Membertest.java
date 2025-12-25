package hellojpa.jpql;

import hellojpa.jpql.domain.Member;
import hellojpa.jpql.domain.MemberDTO;
import hellojpa.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Test
    @Commit
    public void 경로표현식() throws Exception {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Team team2 = new Team();
        team2.setName("teamB");
        em.persist(team2);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        member.setTeam(team);
        em.persist(member);

        Member member2 = new Member();
        member2.setUsername("member2");
        member2.setAge(20);
        member2.setTeam(team);
        em.persist(member2);

        Member member3 = new Member();
        member3.setUsername("member3");
        member3.setAge(30);
        member3.setTeam(team2);
        em.persist(member3);

//        member.changeTeam(team);

        em.flush();
        em.clear();

        String query = "select m from Member m join fetch m.team";
        List<Member> result = em.createQuery(query, Member.class).getResultList();
        for (Member m : result) {
            System.out.println("m = " + m.getUsername() + ", " + m.getTeam().getName());
        }
    }


    @Test
    @Commit
    public void batchSize() throws Exception {
        for (int i=1; i<=100; i++) {
            Team team = new Team();
            team.setName("team" + i);
            em.persist(team);

            Member member = new Member();
            member.setUsername("member" + i);
            member.setAge(i);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();
        }

        String query = "select t from Team t";
        List<Team> resultList = em.createQuery(query, Team.class)
                .setFirstResult(0)
                .getResultList();
        System.out.println("resultList.size = " + resultList.size());

        for (Team t : resultList) {
            System.out.println("team = " + t.getName() + "| Member = " + t.getMembers());
        }


    }

    @Test
    @Commit
    public void 정적쿼리() throws Exception {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Team team2 = new Team();
        team2.setName("teamB");
        em.persist(team2);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        member.setTeam(team);
        em.persist(member);

        Member member2 = new Member();
        member2.setUsername("member2");
        member2.setAge(20);
        member2.setTeam(team);
        em.persist(member2);

        Member member3 = new Member();
        member3.setUsername("member3");
        member3.setAge(30);
        member3.setTeam(team2);
        em.persist(member3);

        em.flush();
        em.clear();

        List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "member1")
                .getResultList();
        for (Member member1 : resultList) {
            System.out.println("member1 = " + member1);
        }

    }

    @Test
    @Commit
    public void 벌크연산() throws Exception {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Team team2 = new Team();
        team2.setName("teamB");
        em.persist(team2);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        member.setTeam(team);
        em.persist(member);

        Member member2 = new Member();
        member2.setUsername("member2");
        member2.setAge(20);
        member2.setTeam(team);
        em.persist(member2);

        Member member3 = new Member();
        member3.setUsername("member3");
        member3.setAge(30);
        member3.setTeam(team2);
        em.persist(member3);

        int resultCount = em.createQuery("update Member m set m.age = 20")
                .executeUpdate();
        System.out.println("resultCount = " + resultCount);

        em.clear();

        System.out.println("member.getAge() = " + member.getAge());
        System.out.println("member.getAge() = " + member2.getAge());
        System.out.println("member.getAge() = " + member3.getAge());

    }
}
