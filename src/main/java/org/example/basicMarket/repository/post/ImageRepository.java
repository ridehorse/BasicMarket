package org.example.basicMarket.repository.post;

import org.example.basicMarket.entity.post.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
