package com.temnet.parser.controller;

import com.temnet.parser.domain.Archive;
import com.temnet.parser.domain.CustomUsers;
import com.temnet.parser.domain.Report;
import com.temnet.parser.repo.ArchiveRepository;
import com.temnet.parser.repo.CustomUsersRepository;
import com.temnet.parser.repo.GroupsRepository;
import com.temnet.parser.service.ArchiveService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Archive Controller class using REST API to present data preprocessed by the corresponding service class*
 *
 * @author Temnet
 */
@RestController
@RequestMapping("api/archive")
public class ArchiveController extends AbstractController<Archive, ArchiveRepository> {
    private final ArchiveService archiveService;
    private final CustomUsersRepository customUsersRepository;
    private final GroupsRepository groupsRepository;

    /**
     * @param archiveService        message archive service class used to process API business logic
     * @param customUsersRepository user repository class used to process API business logic
     * @param groupsRepository      group repository class used to process API business logic
     */
    public ArchiveController(ArchiveRepository repo, ArchiveService archiveService, CustomUsersRepository customUsersRepository, GroupsRepository groupsRepository) {
        super(repo);
        this.archiveService = archiveService;
        this.customUsersRepository = customUsersRepository;
        this.groupsRepository = groupsRepository;
    }

    /**
     * Method that accepts a group ID as input and returns a list of users belonging to this group
     *
     * @param id users group identifier
     * @return list of users included in the group with the specified ID
     * @see GroupsRepository
     * @see CustomUsersRepository
     */
    @GetMapping("/users/{id}")
    public List<String> getUsersByGroup(@PathVariable Long id) {
        List<CustomUsers> allByGroupsByGroupsId = customUsersRepository.findAllByGroupOrderByUsernameAsc(groupsRepository.findByIdOrderByNameAsc(id));
        return allByGroupsByGroupsId.stream().map(CustomUsers::getUsername).collect(Collectors.toList());
    }

    /**
     * @see ArchiveService#getDataByCustomMessage(Long, Long, Timestamp, Timestamp, String, Pageable)
     */
    @GetMapping("/search/{gid}/{uid}/{from}/{to}/{txt}")
    public Page<Report> getDataByCustomMessage(@PathVariable Long gid, @PathVariable Long uid, @PathVariable Timestamp from, @PathVariable Timestamp to, @PathVariable String txt, @PageableDefault Pageable pageable) {
        return archiveService.getDataByCustomMessage(gid, uid, from, to, txt, pageable);
    }

    /**
     * @see ArchiveService#getAllDataByCustomMessage(Timestamp, Timestamp, String, Pageable)
     */
    @GetMapping("/search/all/{from}/{to}/{txt}")
    public Page<Report> getAllDataByCustomMessage(@PathVariable Timestamp from, @PathVariable Timestamp to, @PathVariable String txt, @PageableDefault Pageable pageable) {
        return archiveService.getAllDataByCustomMessage(from, to, txt, pageable);
    }


    /**
     * The method accepts the start date and end date of the search, as well as the text of the message,
     * and returns a paginated representation of the count of all messages for the generated user table
     *
     * @param gid      group identifier
     * @param uid      user identifier
     * @param from     search start date
     * @param to       search end date
     * @param txt      message text
     * @param pageable pagination parameters
     * @return count of all messages
     * @see ArchiveService#getDataByCustomMessage(Long, Long, Timestamp, Timestamp, String, Pageable)
     */
    @GetMapping("/search/{gid}/{uid}/{from}/{to}/{txt}/totalCount")
    public Page<Report> getTotalCountDataByCustomMessage(@PathVariable Long gid, @PathVariable Long uid, @PathVariable Timestamp from, @PathVariable Timestamp to, @PathVariable String txt, @PageableDefault Pageable pageable) {
        List<Report> result = new ArrayList<>();
        Page<Report> dataByCustomMessage = archiveService.getDataByCustomMessage(gid, uid, from, to, txt, pageable);
        result.add(new Report("ВСЕГО", dataByCustomMessage.stream().map(Report::getCount).reduce(0L, Long::sum)));
        return new PageImpl<>(result);
    }

}
