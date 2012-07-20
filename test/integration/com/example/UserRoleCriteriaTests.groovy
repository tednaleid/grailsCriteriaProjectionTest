package com.example

import static org.junit.Assert.*
import org.junit.*

class UserRoleCriteriaTests {

    Role admin

    @Before public void setUp() {
        User foo = User.build(username: "foo")
        User bar = User.build(username: "bar")

        admin = Role.build(authority: "admin")

        UserRole.create(foo, admin)
        UserRole.create(bar, admin)

        User baz = User.build(username: "baz")
        Role peon = Role.build(authority: "peon")
        UserRole.create(baz, peon)
    }

    @Test public void testHQL() {
        List<String> results = UserRole.executeQuery(
                "select u.username from UserRole ur join ur.user u where ur.role = :role",
                [role: admin]
        )
        assert ["bar", "foo"] == results.sort()
    }

    @Test public void testCriteria() {
        List<String> results = UserRole.withCriteria {
            eq("role", admin)
            projections {
                user {
                    property("username")
                }
            }
        }

        /*
        fails with:
        Caused by: org.h2.jdbc.JdbcSQLException: Column "USER_ALIAS1_.USERNAME" not found; SQL statement:
        select user_alias1_.username as y0_ from user_role this_ where this_.role_id=? [42122-164]
         */

        assert ["bar", "foo"] == results.sort()
    }
}
