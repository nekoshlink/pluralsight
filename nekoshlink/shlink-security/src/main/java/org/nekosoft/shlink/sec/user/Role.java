package org.nekosoft.shlink.sec.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.Hibernate;

import javax.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private User user;

    private ShlinkPermission permission;

    public Role() {
        this(null, null, ShlinkPermission.Viewer);
    }

    public Role(User user, ShlinkPermission permission) {
        this.user = user;
        this.permission = permission;
    }

    public Role(ShlinkPermission permission) {
        this.permission = permission;
    }

    public Role(Long id, User user, ShlinkPermission permission) {
        this.id = id;
        this.user = user;
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShlinkPermission getPermission() {
        return permission;
    }

    public void setPermission(ShlinkPermission permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false;
        if (other instanceof Role) {
            Role otherRole = (Role)other;
            return id.equals(otherRole.id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + String.format("(id = %d, user = %s, permission = %s)", id, user, permission);
    }
}
