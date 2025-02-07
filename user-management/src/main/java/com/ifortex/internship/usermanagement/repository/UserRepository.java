package com.ifortex.internship.usermanagement.repository;

import com.ifortex.internship.usermanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  Optional<User> findByUserId(String userId);

  List<User> findByUserIdIn(List<String> userIds);

  default Page<User> findByFilters(String firstName, String lastName, String phone, Pageable pageable) {
    Specification<User> spec = Specification.where(null);

    if (firstName != null && !firstName.isEmpty()) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
    }

    if (lastName != null && !lastName.isEmpty()) {
      spec =
              spec.and(
                      (root, query, cb) ->
                              cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
    }

    if (phone != null && !phone.isEmpty()) {
      spec = spec.and((root, query, cb) -> cb.like(root.get("phoneNumber"), "%" + phone + "%"));
    }

    return findAll(spec, pageable);
  }
}
