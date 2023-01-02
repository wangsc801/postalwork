package pers.wangsc.postalwork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pers.wangsc.postalwork.entity.MayoralHotline;
import pers.wangsc.postalwork.entity.MayoralHotlineLabeled;
import pers.wangsc.postalwork.service.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mayoral_hotline_labeled")
public class MayoralHotlineLabeledController {
    @Value("${mayoral_hotline.files.table.location}")
    private String tableLocation;

    @Value("${mayoral_hotline.files.directory}")
    private String filesDirectory;
    @Autowired
    private ExpressBrandService expressBrandService;
    @Autowired
    private MayoralHotlineService mayoralHotlineService;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private IssueConditionService issueConditionService;
    @Autowired
    private MayoralHotlineLabeledService mayoralHotlineLabeledService;

    @PostMapping("/add_missing_mayoral_hotline")
    public ModelAndView addMissingMayoralHotline() {
        int addMissingMayoralHotlineAmount = mayoralHotlineLabeledService.addMissingMayoralHotline();
        ModelAndView mvList = list(1);
        mvList.addObject("addMissingMayoralHotlineAmount", addMissingMayoralHotlineAmount);
        return mvList;
    }

    @GetMapping("/add_missing_mayoral_hotline")
    public ModelAndView addMissingMayoralHotlineGetMapping(){
        return addMissingMayoralHotline();
    }

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page") int page) {
        int addedFilesCounter = mayoralHotlineService.saveFromFiles(filesDirectory, tableLocation);
        System.out.printf("added %d records.\n", addedFilesCounter);
        List<MayoralHotlineLabeled> hotlineLabeledList = new ArrayList<>();
        Page<MayoralHotline> mayoralHotlinePage = mayoralHotlineService.findIdByOrderByDeadlineDateTimeDesc(page - 1, 100);
        for (MayoralHotline mayoralHotline : mayoralHotlinePage) {
            var labeled = mayoralHotlineLabeledService.findByMayoralHotlineId(mayoralHotline.getId());
            if (labeled != null) {
                hotlineLabeledList.add(labeled);
            }
        }
        ModelAndView mv = new ModelAndView("mayoral_hotline/list");
        mv.addObject("hotlineLabeledList", hotlineLabeledList);
        mv.addObject("totalPage", mayoralHotlinePage.getTotalPages());
        mv.addObject("addedFilesCounter", addedFilesCounter);
        mv.addObject("expressBrandList", expressBrandService.findAll());
        mv.addObject("issueConditionList", issueConditionService.findAll());
        mv.addObject("issueTypeList", issueTypeService.findAll());
        return mv;
    }

    @PostMapping("/update")
    public ModelAndView updateIssueConditionId(@RequestBody MayoralHotlineLabeled mayoralHotlineLabeled) {
        mayoralHotlineLabeledService.update(mayoralHotlineLabeled);
        ModelAndView mv = new ModelAndView("mayoral_hotline/list");
        return mv;
    }
}