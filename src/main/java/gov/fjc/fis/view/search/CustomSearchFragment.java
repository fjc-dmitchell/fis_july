package gov.fjc.fis.view.search;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import gov.fjc.fis.entity.*;
import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.service.*;
import io.jmix.core.LoadContext;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.JpqlCondition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentData;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.fragment.FragmentUtils;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.impl.CollectionContainerImpl;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.*;
import java.util.stream.Collectors;

@FragmentDescriptor("custom-search-fragment.xml")
public class CustomSearchFragment extends Fragment<VerticalLayout> {
    @Autowired
    private SessionData sessionData;
    @Autowired
    private Fragments fragments;

    /**
     * data loaders
     */
    @ViewComponent
    private CollectionLoader<Division> divisionsDl;
    @ViewComponent
    private CollectionLoader<Fund> fundsDl;
    @ViewComponent
    private CollectionLoader<Category> categoriesDl;
    @ViewComponent
    private CollectionLoader<ObjectClass> objectClassesDl;
    @ViewComponent
    private CollectionLoader<Branch> branchesDl;
    @ViewComponent
    private CollectionLoader<Group> groupsDl;

    /**
     * services
     */
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private FundService fundService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ObjectClassService objectClassService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private GroupService groupService;

    /**
     * components
     */
    @ViewComponent("searchTabSheet.customSearchTab")
    private Tab searchTabSheetCustomSearchTab;
    @ViewComponent
    private VerticalLayout customSearchBox;
    @ViewComponent
    private JmixButton showBfyBtn;
    @ViewComponent
    private TypedTextField<String> fiscalYearsField;
    @ViewComponent
    private JmixButton showDiv1Btn;
    @ViewComponent
    private JmixButton showDiv2Btn;
    @ViewComponent
    private JmixButton showDiv3Btn;
    @ViewComponent
    private JmixButton showDiv4Btn;
    @ViewComponent
    private JmixButton showDiv5Btn;
    @ViewComponent
    private JmixButton showDiv6Btn;
    @ViewComponent
    private JmixButton showDiv7Btn;
    @ViewComponent
    private JmixButton showDiv8Btn;
    @ViewComponent
    private EntityComboBox<Fund> fundSearchField;
    @ViewComponent
    private EntityComboBox<Division> divisionSearchField;
    @ViewComponent
    private EntityComboBox<Category> categorySearchField;
    @ViewComponent
    private EntityComboBox<ObjectClass> objectClassSearchField;
    @ViewComponent
    private EntityComboBox<Branch> branchSearchField;
    @ViewComponent
    private EntityComboBox<Group> groupSearchField;
    @ViewComponent
    private HorizontalLayout bocBox;

    /**
     * instance variables
     */
    private CollectionContainer<?> hostContainer;
    private CollectionLoader<?> hostLoader;
    private String hostEntityName;
    private String hostQuery;
    private String fundJoin;
    private String divisionJoin;
    private String categoryJoin;
    private String objectClassJoin;
    private String obligationJoin;
    private String branchJoin;
    private String groupJoin;
    private List<Condition> customConditions = new ArrayList<>();
    private List<Condition> filterConditions = new ArrayList<>();
    //    private FragmentData fragmentData;
    private Fragment<VerticalLayout> fragment;
    private List<Appropriation> fiscalYears;
    private String divisionCode;
    private String masterObjectClass;
    private boolean fjcFoundation;
    private Fund fjcFoundationFund;

    /**
     * The hostDataContainer property must be explicitly set by the host invoking the fragment
     *
     * @param hostDataContainer
     */
    public void setHostDataContainer(CollectionContainer<?> hostDataContainer) {
        hostContainer = hostDataContainer;
        hostLoader = (CollectionLoader<?>) ((CollectionContainerImpl<?>) hostDataContainer).getLoader();
        // todo: create a hostloader if it doesn't exist?
        if (hostLoader == null) {
            throw new IllegalStateException("hostLoader is null in SearchFragment");
        }
        Class<?> hostClass = hostDataContainer.getEntityMetaClass().getJavaClass();
        hostEntityName = hostDataContainer.getEntityMetaClass().getName();
    }

    /**
     * fjcFoundation is optional but is needed by most entities
     *
     * @param fjcFoundation limits search to Foundation entities
     */
    public void setFjcFoundation(boolean fjcFoundation) {
        this.fjcFoundation = fjcFoundation;
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostAttach(final AttachEvent event) {
        if (hostContainer == null) {
            throw new IllegalStateException("hostContainer is null in SearchFragment");
        }
        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        fjcFoundationFund = fundService.getFoundationFund();
        setBfyBtnCaption();
        fundsDl.load(); // are these necessary?
        divisionsDl.load();
        categoriesDl.load();
        objectClassesDl.load();
        branchesDl.load();
        groupsDl.load();
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostBeforeShow(final View.BeforeShowEvent event) {
//        configureSubFragments();
        if(fjcFoundation) {
//            fundSearchField.setItems(fjcFoundationFund);
            fundSearchField.setValue(fjcFoundationFund);
            fundSearchField.setReadOnly(true);
            // set visibility of division box?
        }
        configureHostEntity();
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        //perform all loads here?
        performSearch();
    }



//    private void configureSubFragments() {
////        switch (hostEntityName) {
////            case "fis_Activity" ->
////                    configureEntitySearchFragment(ActivitySearchFragment.class, "activitiesDc", "activitiesDl");
////            case "fis_Obligation" ->
////                    configureEntitySearchFragment(ObligationSearchFragment.class, "obligationsDc", "obligationsDl");
////            case "fis_Invoice" ->
////                    configureEntitySearchFragment(InvoiceSearchFragment.class, "invoicesDc", "invoicesDl");
////            case "fis_FundControlNotice" ->
////                    configureEntitySearchFragment(FcnSearchFragment.class, "fundControlNoticesDc", "fundControlNoticesDl");
////        }
////        String dataContainerId;
////        String dataLoaderId;
////        Class<? extends Fragment<VerticalLayout>> fragmentClass;
////        FragmentData fragmentData = this.getFragmentData();
//        switch (hostEntityName) {
//            case "fis_Activity":
////                fragmentClass = ActivitySearchFragment.class;
////                dataContainerId = "activitiesDc";
////                dataLoaderId = "activitiesDl";
//                configureEntitySearchFragment(ActivitySearchFragment.class, "activitiesDc", "activitiesDl");
//                break;
//            case "fis_Obligation":
////                fragmentClass = ObligationSearchFragment.class;
////                dataContainerId = "obligationsDc";
////                dataLoaderId = "obligationsDl";
//                configureEntitySearchFragment(ObligationSearchFragment.class, "obligationsDc", "obligationsDl");
//                break;
//            case "fis_Invoice":
////                fragmentClass = InvoiceSearchFragment.class;
////                dataContainerId = "invoicesDc";
////                dataLoaderId = "invoicesDl";
//                configureEntitySearchFragment(InvoiceSearchFragment.class, "invoicesDc", "invoicesDl");
//                break;
//            case "fis_FundControlNotice":
////                fragmentClass = FcnSearchFragment.class;
////                dataContainerId = "fundControlNoticesDc";
////                dataLoaderId = "fundControlNoticesDl";
//                configureEntitySearchFragment(FcnSearchFragment.class, "fundControlNoticesDc", "fundControlNoticesDl");
//                break;
////            default:
////                throw new RuntimeException("Unknown host entity in SearchFragment controller: " + hostEntityName);
//        }
////        fragmentData.registerContainer(dataContainerId, hostContainer);
////        fragmentData.registerLoader(dataLoaderId, hostLoader);
////        FragmentUtils.setFragmentData(this, fragmentData);
////        fragment = fragments.create(this, fragmentClass);
////        ((EntitySearchFragment) fragment).addCategoryObjectClass(categorySearchField, objectClassSearchField);
////        ((EntitySearchFragment) fragment).addBranchGroup(branchSearchField, groupSearchField);
//////        filterConditions = ((ActivitySearchFragment) fragment).getCustomConditions();
//////        filterConditions = ((EntitySearchFilter) fragment).getCustomConditions();
////        filterConditions = ((EntitySearchFragment) fragment).getCustomConditions();
////        customSearchBox.add(fragment);
//    }

    private void configureEntitySearchFragment(Class<? extends Fragment<VerticalLayout>> fragmentClass,
                                               String dataContainerId,
                                               String dataLoaderId) {
        FragmentData fragmentData = this.getFragmentData();
        searchTabSheetCustomSearchTab.setVisible(true);

        fragmentData.registerContainer(dataContainerId, hostContainer);
        fragmentData.registerLoader(dataLoaderId, hostLoader);
        FragmentUtils.setFragmentData(this, fragmentData);
        fragment = fragments.create(this, fragmentClass);
        ((EntitySearchFragment) fragment).addCategoryObjectClass(categorySearchField, objectClassSearchField);
        ((EntitySearchFragment) fragment).addBranchGroup(branchSearchField, groupSearchField);
//        filterConditions = ((ActivitySearchFragment) fragment).getCustomConditions();
//        filterConditions = ((EntitySearchFilter) fragment).getCustomConditions();
        filterConditions = ((EntitySearchFragment) fragment).getCustomConditions();
        customSearchBox.add(fragment);
    }

    private void configureHostEntity() {
        hostQuery = "SELECT e FROM ".concat(hostEntityName).concat(" e");
        switch (hostEntityName) {
            case "fis_Activity":
                fundJoin = "JOIN {E}.fund f";
                divisionJoin = "JOIN {E}.division d";
                categoryJoin = "JOIN {E}.projections p JOIN p.objectClass b JOIN b.category m";
                objectClassJoin = "JOIN {E}.projections p JOIN p.objectClass b";
                branchJoin = "JOIN {E}.branch bch";
                groupJoin = "JOIN {E}.group grp";
                configureEntitySearchFragment(ActivitySearchFragment.class, "activitiesDc", "activitiesDl");
                break;
            case "fis_Obligation":
                fundJoin = "JOIN {E}.activity a JOIN a.fund f";
                divisionJoin = "JOIN {E}.activity a JOIN a.division d";
                categoryJoin = "JOIN {E}.objectClass b JOIN b.category m";
                objectClassJoin = "JOIN {E}.objectClass b";
                branchJoin = "JOIN {E}.activity a JOIN a.branch bch";
                groupJoin = "JOIN {E}.activity a JOIN a.group grp";
                configureEntitySearchFragment(ObligationSearchFragment.class, "obligationsDc", "obligationsDl");
                break;
            case "fis_Invoice":
                fundJoin = "JOIN {E}.obligation o JOIN o.activity a JOIN a.fund f";
                divisionJoin = "JOIN {E}.obligation o JOIN o.activity a JOIN a.division d";
                categoryJoin = "JOIN {E}.obligation o JOIN o.objectClass b JOIN b.category m";
                objectClassJoin = "JOIN {E}.obligation o JOIN o.objectClass b";
                obligationJoin = "JOIN {E}.obligation o";
                configureEntitySearchFragment(InvoiceSearchFragment.class, "invoicesDc", "invoicesDl");
                break;
            case "fis_FundControlNotice":
                fundJoin = "JOIN {E}.obligation o JOIN o.activity a JOIN a.fund f";
                divisionJoin = "JOIN {E}.obligation o JOIN o.activity a JOIN a.division d";
                categoryJoin = "JOIN {E}.obligation o JOIN o.objectClass b JOIN b.category m";
                objectClassJoin = "JOIN {E}.obligation o JOIN o.objectClass b";
                obligationJoin = "JOIN {E}.obligation o";
                configureEntitySearchFragment(FcnSearchFragment.class, "fundControlNoticesDc", "fundControlNoticesDl");
                break;
            case "fis_Division":
                hostQuery = "SELECT d FROM fis_Division d";
                fundJoin = "JOIN {E}.fund f";
                break;
            default:
                fundJoin = null;
                divisionJoin = null;
        }
    }

    private void setBfyBtnCaption() {
        if (fiscalYears.size() == 1) {
            showBfyBtn.setText("Show all for " + fiscalYears.get(0).getBudgetFiscalYear());
        } else {
            showBfyBtn.setText("Show Search BFYs");
        }
        fiscalYearsField.setValue(fiscalYears.stream().map(Appropriation::getBudgetFiscalYear).collect(Collectors.joining(", ")));
    }

    @Install(to = "fundsDl", target = Target.DATA_LOADER)
    protected List<Fund> fundsDlLoadDelegate(final LoadContext<Fund> loadContext) {
        return fundService.getFundSearchList(fjcFoundation);
    }

    @Install(to = "divisionsDl", target = Target.DATA_LOADER)
    protected List<Division> divisionsDlLoadDelegate(final LoadContext<Division> loadContext) {
        List<Division> divisionList = divisionService.getDivisionSearchList(fiscalYears, fjcFoundation);
        Set<String> divisionCodes = divisionList.stream().map(Division::getDivisionCode).collect(Collectors.toSet());
        showDiv1Btn.setVisible(divisionCodes.contains("1"));
        showDiv2Btn.setVisible(divisionCodes.contains("2"));
        showDiv3Btn.setVisible(divisionCodes.contains("3"));
        showDiv4Btn.setVisible(divisionCodes.contains("4"));
        showDiv5Btn.setVisible(divisionCodes.contains("5"));
        showDiv6Btn.setVisible(divisionCodes.contains("6"));
        showDiv7Btn.setVisible(divisionCodes.contains("7"));
        showDiv8Btn.setVisible(divisionCodes.contains("8"));
        return divisionList;
    }

    @Install(to = "categoriesDl", target = Target.DATA_LOADER)
    protected List<Category> categoriesDlLoadDelegate(final LoadContext<Category> loadContext) {
        return categoryService.getCategorySearchList(fiscalYears);
    }

    @Install(to = "objectClassesDl", target = Target.DATA_LOADER)
    protected List<ObjectClass> objectClassesDlLoadDelegate(final LoadContext<ObjectClass> loadContext) {
        return objectClassService.getObjectClassSearchList(fiscalYears, masterObjectClass, false);
    }

    @Install(to = "branchesDl", target = Target.DATA_LOADER)
    protected List<Branch> branchesDlLoadDelegate(final LoadContext<Branch> loadContext) {
        return branchService.getBranchSearchList(fiscalYears, divisionCode);
    }

    @Install(to = "groupsDl", target = Target.DATA_LOADER)
    protected List<Group> groupsDlLoadDelegate(final LoadContext<Group> loadContext) {
        return groupService.getGroupSearchList(fiscalYears, divisionCode);
    }

    @Install(to = "divisionSearchField", subject = "itemLabelGenerator")
    protected Object divisionSearchFieldItemLabelGenerator(final Division division) {
        return division.getTitleAndCode();
    }

    @Install(to = "categorySearchField", subject = "itemLabelGenerator")
    protected Object categorySearchFieldItemLabelGenerator(final Category category) {
        return category.getTitleAndCode();
    }

    @Install(to = "objectClassSearchField", subject = "itemLabelGenerator")
    protected Object objectClassSearchFieldItemLabelGenerator(final ObjectClass objectClass) {
        return objectClass.getTitleAndCode();
    }

    @Install(to = "branchSearchField", subject = "itemLabelGenerator")
    protected Object branchSearchFieldItemLabelGenerator(final Branch branch) {
        return branch.getTitleAndCode();
    }

    @Install(to = "groupSearchField", subject = "itemLabelGenerator")
    protected Object groupSearchFieldItemLabelGenerator(final Group group) {
        return group.getTitleAndCode();
    }

    @Subscribe("divisionSearchField")
    protected void onDivisionSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Division>, Division> event) {
        if (event.getValue() == null) {
            divisionCode = null;
//            branchSearchField.setValue(null);
//            groupSearchField.setValue(null);
        } else {
            divisionCode = event.getValue().getDivisionCode();
        }
        branchSearchField.setValue(null);
        groupSearchField.setValue(null);
        branchesDl.load();
        groupsDl.load();
    }

    @Subscribe("categorySearchField")
    protected void onCategorySearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        if (categorySearchField.getValue() == null) {
            masterObjectClass = null;
        } else {
            if (objectClassSearchField.getValue() == null) {
                masterObjectClass = Objects.requireNonNull(event.getValue().getMasterObjectClass());
            } else {
                if (!categorySearchField.getValue().getMasterObjectClass().equals(objectClassSearchField.getValue().getCategory().getMasterObjectClass())) {
                    objectClassSearchField.setValue(null);
                }
                masterObjectClass = event.getValue().getMasterObjectClass();
            }
        }
        objectClassesDl.load();
    }

    @Subscribe("showDivisionAction")
    public void onShowDivisionAction(final ActionPerformedEvent event) {
        if (FragmentUtils.getComponentId(event.getComponent()).isPresent()) {
            String btnId = FragmentUtils.getComponentId(event.getComponent()).get();
            switch (btnId) {
                case "showDiv1Btn" -> hostLoader.setParameter("divCodeFilterField", "1");
                case "showDiv2Btn" -> hostLoader.setParameter("divCodeFilterField", "2");
                case "showDiv3Btn" -> hostLoader.setParameter("divCodeFilterField", "3");
                case "showDiv4Btn" -> hostLoader.setParameter("divCodeFilterField", "4");
                case "showDiv5Btn" -> hostLoader.setParameter("divCodeFilterField", "5");
                case "showDiv6Btn" -> hostLoader.setParameter("divCodeFilterField", "6");
                case "showDiv7Btn" -> hostLoader.setParameter("divCodeFilterField", "7");
                case "showDiv8Btn" -> hostLoader.setParameter("divCodeFilterField", "8");
            }
            performSearch();
            hostLoader.removeParameter("divCodeFilterField");
        }
    }

    @Subscribe(id = "showBfyBtn", subject = "clickListener")
    protected void onShowBfyBtnClick(final ClickEvent<JmixButton> event) {
        performSearch();
    }

    @Subscribe(id = "customSearchBtn", subject = "clickListener")
    protected void onCustomSearchBtnClick(final ClickEvent<JmixButton> event) {
        if (fundSearchField.getValue() != null) {
            hostLoader.setParameter("fundFilterField", fundSearchField.getValue());
        }
        if (divisionSearchField.getValue() != null) {
            hostLoader.setParameter("divCodeFilterField", divisionSearchField.getValue().getDivisionCode());
        }
        if (categorySearchField.getValue() != null) {
            hostLoader.setParameter("mocFilterField", categorySearchField.getValue().getMasterObjectClass());
        }
        if (objectClassSearchField.getValue() != null) {
            hostLoader.setParameter("bocFilterField", objectClassSearchField.getValue().getBudgetObjectClass());
        }
        if (branchSearchField.getValue() != null) {
            hostLoader.setParameter("branchCodeFilterField", branchSearchField.getValue().getBranchCode());
        } else {
            hostLoader.removeParameter("branchCodeFilterField");
        }

        if (groupSearchField.getValue() != null) {
            hostLoader.setParameter("groupCodeFilterField", groupSearchField.getValue().getGroupCode());
        } else {
            hostLoader.removeParameter("groupCodeFilterField");
        }
        performSearch();
    }

    @Subscribe(id = "clearSearchBtn", subject = "clickListener")
    protected void onClearSearchBtnClick(final ClickEvent<JmixButton> event) {
        clearCustomSearchParameters();
        clearCustomSearchFields();
    }

    private void clearCustomSearchParameters() {
        Set<String> params = new HashSet<>(hostLoader.getParameters().keySet());
        params.forEach(hostLoader::removeParameter);
    }

    private void clearCustomSearchFields() {
        divisionSearchField.setValue(null);
        if(!fjcFoundation) {
            fundSearchField.setValue(null);
        }
        ((EntitySearchFragment) fragment).clearSearchFilters();

//        customFilters.forEach((key, value) -> value.setValue(null));
    }

    private void performSearch() {
        if(fjcFoundation) {
            customConditions.add(JpqlCondition.createWithParameters("f = :foundationFund", fundJoin, Map.of("foundationFund", fjcFoundationFund)));
        } else {
            customConditions.add(JpqlCondition.createWithParameters("f <> :foundationFund", fundJoin, Map.of("foundationFund", fjcFoundationFund)));
        }
        customConditions.add(JpqlCondition.createWithParameters("d.appropriation in :bfyFilterField", divisionJoin, Map.of("bfyFilterField", fiscalYears)));
        customConditions.add(JpqlCondition.create("f = :fundFilterField", fundJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("d.divisionCode = :divCodeFilterField", divisionJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("m.masterObjectClass = :mocFilterField", categoryJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("b.budgetObjectClass = :bocFilterField", objectClassJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("bch.branchCode = :branchCodeFilterField", branchJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("grp.groupCode = :groupCodeFilterField", groupJoin).skipNullOrEmpty());
        customConditions.addAll(filterConditions);

        hostLoader.setQuery(hostQuery);
        hostLoader.setCondition(LogicalCondition.and(customConditions.toArray(new Condition[0])));
        hostLoader.setFirstResult(0);
        hostLoader.load();
        customConditions.clear();
        clearCustomSearchParameters();
    }

    /**
     * Changes the Show BFY button caption after a Fiscal Year change event
     *
     * @param event
     */
    @Async
    @EventListener
    public void handleAsyncEvent(FiscalYearChangeEvent event) {
        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        setBfyBtnCaption();
        divisionsDl.load();
        if (divisionSearchField.getValue() != null) {
            divisionSearchField.setValue(
                    divisionsDl.getContainer().getItems().stream()
                            .filter(div -> div.getDivisionCode().equals(divisionSearchField.getValue().getDivisionCode()))
                            .findFirst()
                            .orElse(null)
            );
        }
        categoriesDl.load();
        if (categorySearchField.getValue() != null) {
            categorySearchField.setValue(
                    categoriesDl.getContainer().getItems().stream()
                            .filter(cat -> cat.getMasterObjectClass().equals(categorySearchField.getValue().getMasterObjectClass()))
                            .findFirst()
                            .orElse(null)
            );
        }
        objectClassesDl.load();
        if (objectClassSearchField.getValue() != null) {
            objectClassSearchField.setValue(
                    objectClassesDl.getContainer().getItems().stream()
                            .filter(boc -> boc.getBudgetObjectClass().equals(objectClassSearchField.getValue().getBudgetObjectClass()))
                            .findFirst()
                            .orElse(null)
            );
        }
        if (hostEntityName.equals("fis_Activity")) {
            if (branchSearchField.getValue() != null) {
                branchSearchField.setValue(
                        branchesDl.getContainer().getItems().stream()
                                .filter(bch -> bch.getBranchCode().equals(branchSearchField.getValue().getBranchCode()))
                                .findFirst()
                                .orElse(null)
                );
            }
            if (groupSearchField.getValue() != null) {
                groupSearchField.setValue(
                        groupsDl.getContainer().getItems().stream()
                                .filter(grp -> grp.getGroupCode().equals(groupSearchField.getValue().getGroupCode()))
                                .findFirst()
                                .orElse(null)
                );
            }
        }
    }

    @Subscribe("searchTabSheet")
    protected void onSearchTabSheetSelectedChange(final JmixTabSheet.SelectedChangeEvent event) {
        clearCustomSearchParameters();
        clearCustomSearchFields();
    }
}