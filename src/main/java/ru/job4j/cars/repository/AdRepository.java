package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.Post;

import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

public class AdRepository implements Store, AutoCloseable {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    private static final class Lazy {
        private static final AdRepository INST = new AdRepository();
    }

    public static AdRepository instOf() {
        return Lazy.INST;
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Collection<Post> getAllPosts() {
        return tx(
                session -> session.createQuery(
                        "select distinct post from Post post "
                                + "join fetch post.model model "
                                + "join fetch model.mark "
                                + "join fetch post.body "
                                + "join fetch post.transmission "
                                + "join fetch post.user "
                                + "join fetch post.photo ", Post.class)
                        .list()
        );
    }

    @Override
    public Collection<Post> getPostsForLastDay() {
        return tx(
                session -> session.createQuery(
                        "select distinct post from Post post "
                                + "join fetch post.model model "
                                + "join fetch model.mark "
                                + "join fetch post.body "
                                + "join fetch post.transmission "
                                + "join fetch post.user "
                                + "join fetch post.photo "
                                + "where post.created > :todayParam", Post.class)
                        .setParameter("todayParam",
                                new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)))
                        .list()
        );
    }

    @Override
    public Collection<Post> getPostsByMark(String mark) {
        return tx(
                session -> session.createQuery(
                        "select distinct post from Post post "
                                + "join fetch post.model model "
                                + "join fetch model.mark "
                                + "join fetch post.body "
                                + "join fetch post.transmission "
                                + "join fetch post.user "
                                + "join fetch post.photo "
                                + "join Mark mark on mark.id = post.model.id "
                                + "where mark.name =: markName", Post.class)
                        .setParameter("markName", mark)
                        .list()
        );
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
