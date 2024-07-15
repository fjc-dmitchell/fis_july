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
    @ViewComponent
    private HorizontalLayout divisionSearchButtons;
    @ViewComponent
    private HorizontalLayout divFundBox;

    /**
     * instance variables
     */
    private CollectionContainer<?> hostContainer;
    private CollectionLoader<?> hostLoader;
    private String hostEntityName;
    private String hostEntityQuery;
    private String fundJoin;
    private String appropriationJoin;
    private String divisionJoin;
    private String categoryJoin;
    private String objectClassJoin;
    private String obligationJoin;
    private String branchJoin;
    private String groupJoin;
    //    private List<Condition> customConditions = new ArrayList<>();
    private List<Condition> filterConditions = new ArrayList<>();
    //    private FragmentData fragmentData;
    private Fragment<VerticalLayout> fragment;
    private List<Appropriation> fiscalYears;
    private String divisionCode;
    private String masterObjectClass;
    private boolean fjcFoundation;
    private Fund fjcFoundationFund;

    /**
     * The hostDataContainer property must be explicitly set by the host invoking the fragment.
     * The hostDataContainer <em>must</em> have a dataLoader
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
        Class<?> hostEntityClass = hostDataContainer.getEntityMetaClass().getJavaClass();
        hostEntityName = hostDataContainer.getEntityMetaClass().getName();
//        fiscalYears = appropriationService.getBfyFilterField(sessionData);
//        fjcFoundationFund = fundService.getFoundationFund();
//        performSearch();
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
        configureHostEntity();
        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        fjcFoundationFund = fundService.getFoundationFund();
        setBfyBtnCaption();
        fundsDl.load();
        divisionsDl.load();
        categoriesDl.load();
        objectClassesDl.load();
        branchesDl.load();
        groupsDl.load();
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        if (fjcFoundation) {
            fundSearchField.setValue(fjcFoundationFund);
            fundSearchField.setReadOnly(true);
            // set visibility of division box?
        }
        performSearch();
    }

    private void configureHostEntity() {
        hostEntityQuery = "SELECT e FROM ".concat(hostEntityName).concat(" e");
        switch (hostEntityName) {
            case "fis_Activity":
                hostEntityQuery += " ORDER BY e.division.appropriation.budgetFiscalYear, e.division.divisionCode, e.activityNumber";
                fundJoin = "JOIN {E}.fund f";
                appropriationJoin = "JOIN {E}.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.division dv";
                categoryJoin = "JOIN {E}.projections p JOIN p.objectClass obj JOIN obj.category cat";
                objectClassJoin = "JOIN {E}.projections p JOIN p.objectClass obj";
                branchJoin = "JOIN {E}.branch bch";
                groupJoin = "JOIN {E}.group grp";
                configureEntitySearchFragment(ActivitySearchFragment.class, "activitiesDc", "activitiesDl");
                hostLoader.setFetchPlan("activity-search-fetch-plan");
                break;
            case "fis_Obligation":
                hostEntityQuery += " ORDER BY e.activity.division.appropriation.budgetFiscalYear, e.activity.division.divisionCode, e.documentNumber, e.objectClass.budgetObjectClass";
                fundJoin = "JOIN {E}.activity act JOIN act.fund f";
                appropriationJoin = "JOIN {E}.activity act JOIN act.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.activity act JOIN act.division dv";
                categoryJoin = "JOIN {E}.objectClass obj JOIN obj.category cat";
                objectClassJoin = "JOIN {E}.objectClass obj";
                branchJoin = "JOIN {E}.activity act JOIN act.branch bch";
                groupJoin = "JOIN {E}.activity act JOIN act.group grp";
                configureEntitySearchFragment(ObligationSearchFragment.class, "obligationsDc", "obligationsDl");
                hostLoader.setFetchPlan("obligation-search-fetch-plan");
                break;
            case "fis_Invoice":
                hostEntityQuery += " ORDER BY e.obligation.activity.division.appropriation.budgetFiscalYear, e.obligation.activity.division.divisionCode, e.obligation.documentNumber, e.obligation.objectClass.budgetObjectClass, e.invoiceNumber";
                fundJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.fund f";
                appropriationJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.division dv";
                categoryJoin = "JOIN {E}.obligation obl JOIN obl.objectClass obj JOIN obj.category cat";
                objectClassJoin = "JOIN {E}.obligation obl JOIN obl.objectClass obj";
                obligationJoin = "JOIN {E}.obligation obl";
                configureEntitySearchFragment(InvoiceSearchFragment.class, "invoicesDc", "invoicesDl");
                hostLoader.setFetchPlan("invoice-search-fetch-plan");
                break;
            case "fis_FundControlNotice":
                hostEntityQuery += " ORDER BY e.obligation.activity.division.appropriation.budgetFiscalYear, e.obligation.activity.division.divisionCode, e.obligation.documentNumber, e.obligation.objectClass.budgetObjectClass, e.fcnDate";
                obligationJoin = " JOIN {E}.obligation obl";
                objectClassJoin = obligationJoin.concat(" JOIN obl.objectClass obj");
                categoryJoin = objectClassJoin.concat(" JOIN obj.category cat");
                var activityJoin = obligationJoin.concat(" JOIN obl.activity act");
                fundJoin = activityJoin.concat(" JOIN act.fund f");
                divisionJoin = activityJoin.concat(" JOIN act.division dv");
                appropriationJoin = divisionJoin.concat(" JOIN dv.appropriation app");
                configureEntitySearchFragment(FcnSearchFragment.class, "fundControlNoticesDc", "fundControlNoticesDl");
                hostLoader.setFetchPlan("fundControlNotice-search-fetch-plan");
                break;
            case "fis_Division":
                hostEntityQuery += " ORDER BY e.appropriation.budgetFiscalYear, e.divisionCode";
                appropriationJoin = "JOIN {E}.appropriation app";
                hostEntityQuery = "SELECT dv FROM fis_Division dv";
//                hostEntityQuery = "SELECT e FROM fis_Division e ORDER BY e.appropriation.budgetFiscalYear, e.divisionCode";
                fundJoin = "JOIN dv.fund f";

                break;
            case "fis_Branch":
                hostEntityQuery += " ORDER BY e.division.appropriation.budgetFiscalYear, e.division.divisionCode, e.branchCode";
                fundJoin = "JOIN {E}.division.fund f";
                appropriationJoin = "JOIN {E}.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.division d";
//                hostEntityQuery += " ORDER BY d.appropriation.budgetFiscalYear, d.divisionCode, e.branchCode";
                break;
            case "fis_Group":
                hostEntityQuery += " ORDER BY e.division.appropriation.budgetFiscalYear, e.division.divisionCode, e.groupCode";
                fundJoin = "JOIN {E}.division.fund f";
                appropriationJoin = "JOIN {E}.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.division d";
//                hostEntityQuery += " ORDER BY d.appropriation.budgetFiscalYear, d.divisionCode, e.groupCode";
                break;
            case "fis_Category":
                hostEntityQuery += " ORDER BY e.appropriation.budgetFiscalYear, e.masterObjectClass";
                fundJoin = null;
                appropriationJoin = "JOIN {E}.appropriation app";
//                hostEntityQuery = "SELECT cat FROM fis_Category cat ORDER BY cat.appropriation.budgetFiscalYear, cat.masterObjectClass";
                divisionSearchButtons.setVisible(false);
                break;
            case "fis_ObjectClass":
                hostEntityQuery += " ORDER BY e.category.appropriation.budgetFiscalYear, e.category.masterObjectClass, e.budgetObjectClass";
                fundJoin = null;
                appropriationJoin = "JOIN {E}.category cat JOIN cat.appropriation app";
                categoryJoin = "JOIN {E}.category cat";
//                hostEntityQuery = "SELECT o FROM fis_ObjectClass o";
//                hostEntityQuery += " JOIN o.category d";
//                hostEntityQuery += " ORDER BY d.appropriation.budgetFiscalYear, d.masterObjectClass, o.budgetObjectClass";
                divisionSearchButtons.setVisible(false);
//                searchTabSheetCustomSearchTab.setVisible(true);
                divFundBox.setVisible(false);
//                bocBox.setVisible(true);
                break;
            default:
                throw new IllegalStateException(hostEntityName.concat(" has not been configured in CustomSearchFragment"));
        }
    }

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
        filterConditions = ((EntitySearchFragment) fragment).getCustomConditions();
        customSearchBox.add(fragment);
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

    @Subscribe("fundSearchField")
    protected void onFundSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Fund>, Fund> event) {
        if (fundSearchField.getValue() == null) {
            hostLoader.removeParameter("fundFilterField");
        }
    }

    @Subscribe("divisionSearchField")
    protected void onDivisionSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Division>, Division> event) {
        // if year added, should check value of existing branch/group, not necessarily erase it.
        if (event.getValue() == null) {
            divisionCode = null;
            hostLoader.removeParameter("divCodeFilterField");
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

    @Subscribe("branchSearchField")
    protected void onBranchSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Branch>, Branch> event) {
        if (branchSearchField.getValue() == null) {
            hostLoader.removeParameter("branchCodeFilterField");
        }
    }

    @Subscribe("groupSearchField")
    protected void onGroupSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Group>, Group> event) {
        if (groupSearchField.getValue() == null) {
            hostLoader.removeParameter("groupCodeFilterField");
        }
    }

    @Subscribe("categorySearchField")
    protected void onCategorySearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        if (categorySearchField.getValue() == null) {
            masterObjectClass = null;
            hostLoader.removeParameter("mocFilterField");
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

    @Subscribe("objectClassSearchField")
    protected void onObjectClassSearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<ObjectClass>, ObjectClass> event) {
        if (objectClassSearchField.getValue() == null) {
            hostLoader.removeParameter("bocFilterField");
        }
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
//            hostLoader.removeParameter("divCodeFilterField");
        }
    }

    @Subscribe(id = "showBfyBtn", subject = "clickListener")
    protected void onShowBfyBtnClick(final ClickEvent<JmixButton> event) {
        hostLoader.removeParameter("divCodeFilterField");
        performSearch();
    }

    @Subscribe(id = "customSearchBtn", subject = "clickListener")
    protected void onCustomSearchBtnClick(final ClickEvent<JmixButton> event) {
        if (fundSearchField.getValue() != null) {
            hostLoader.setParameter("fundFilterField", fundSearchField.getValue());
        }
        if (divisionSearchField.getValue() != null) {
            hostLoader.setParameter("divCodeFilterField", divisionSearchField.getValue().getDivisionCode());
//        } else {
//            hostLoader.removeParameter("divCodeFilterField");
        }
        if (categorySearchField.getValue() != null) {
            hostLoader.setParameter("mocFilterField", categorySearchField.getValue().getMasterObjectClass());
        }
        if (objectClassSearchField.getValue() != null) {
            hostLoader.setParameter("bocFilterField", objectClassSearchField.getValue().getBudgetObjectClass());
        }
        if (branchSearchField.getValue() != null) {
            hostLoader.setParameter("branchCodeFilterField", branchSearchField.getValue().getBranchCode());
        }
        if (groupSearchField.getValue() != null) {
            hostLoader.setParameter("groupCodeFilterField", groupSearchField.getValue().getGroupCode());
        }
        performSearch();
//        clearCustomSearchParameters();
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
        if (!fjcFoundation) {
            fundSearchField.setValue(null);
        }
        if (fragment != null) {
            ((EntitySearchFragment) fragment).clearSearchFilters();
        }

//        customFilters.forEach((key, value) -> value.setValue(null));
    }

    private void performSearch() {
        List<Condition> customConditions = new ArrayList<>();
        customConditions.clear();
//        clearCustomSearchParameters();

        if (fundJoin != null) {
            if (hostLoader.getParameter("fundFilterField") != null) {
                customConditions.add(JpqlCondition.create("f = :fundFilterField", fundJoin));
            } else {

                if (fjcFoundation) {
                    customConditions.add(JpqlCondition.createWithParameters("f = :foundationFund", fundJoin, Map.of("foundationFund", fjcFoundationFund)));
                } else {
                    customConditions.add(JpqlCondition.createWithParameters("f <> :foundationFund", fundJoin, Map.of("foundationFund", fjcFoundationFund)));
                }
            }
        }
//            if (fjcFoundation) {
//                hostLoader.setParameter("fundFilterField", fjcFoundationFund);
//                customConditions.add(JpqlCondition.create("f = :fundFilterField", fundJoin));
//            } else {
//                if (hostLoader.getParameter("fundFilterField") != null) {
//                    customConditions.add(JpqlCondition.create("f = :fundFilterField", fundJoin));
//                } else {
//                    hostLoader.setParameter("fundFilterField", fjcFoundationFund);
//                    customConditions.add(JpqlCondition.create("f <> :fundFilterField", fundJoin));
//                }
//            }
//        }
//        if(hostLoader.getParameter("fundFilterField")==null) {
//            hostLoader.setParameter("fundFilterField", fjcFoundationFund);
//        }
//        if(hostLoader.getParameter("fundFilterField") == null) {
//            hostLoader.setParameter("fundFilterField", fjcFoundationFund);
//            customConditions.add(JpqlCondition.create("f <> :fundFilterField", fundJoin));
//        } else {
//            customConditions.add(JpqlCondition.create("f = :fundFilterField", fundJoin));
//        }

//        if (fundJoin != null) {
//            if (fjcFoundation) {
//                customConditions.add(JpqlCondition.create("f = :fundFilterField", fundJoin));
//            } else {
//                customConditions.add(JpqlCondition.create("f <> :fundFilterField", fundJoin));
//            }
//        }

        hostLoader.setParameter("bfyFilterField", fiscalYears);

//        customConditions.add(JpqlCondition.createWithParameters("app in :bfyFilterField", appropriationJoin, Map.of("bfyFilterField", fiscalYears)));
        customConditions.add(JpqlCondition.create("app in :bfyFilterField", appropriationJoin).skipNullOrEmpty());
//        customConditions.add(JpqlCondition.create("dv.divisionCode = :divCodeFilterField", divisionJoin).skipNullOrEmpty());
        if (hostLoader.getParameter("divCodeFilterField") != null) {
            customConditions.add(JpqlCondition.createWithParameters("dv.divisionCode = :divCodeFilterField", divisionJoin, hostLoader.getParameters()));
        }
        customConditions.add(JpqlCondition.create("cat.masterObjectClass = :mocFilterField", categoryJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("obj.budgetObjectClass = :bocFilterField", objectClassJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("bch.branchCode = :branchCodeFilterField", branchJoin).skipNullOrEmpty());
        customConditions.add(JpqlCondition.create("grp.groupCode = :groupCodeFilterField", groupJoin).skipNullOrEmpty());
        customConditions.addAll(filterConditions);

        hostLoader.setQuery(hostEntityQuery);
        hostLoader.setCondition(LogicalCondition.and(customConditions.toArray(new Condition[0])));
//        hostLoader.setCondition(
//                LogicalCondition.and(
//                        JpqlCondition.createWithParameters("app in :bfyFilterField", appropriationJoin, Map.of("bfyFilterField", fiscalYears)),
//                        JpqlCondition.create("dv.divisionCode = :divCodeFilterField", divisionJoin).skipNullOrEmpty(),
//                        JpqlCondition.create("f = :fundFilterField", fundJoin).skipNullOrEmpty()
//                )
//        );
        hostLoader.setFirstResult(0);
        hostLoader.load();
//        customConditions.clear();
//        clearCustomSearchParameters();
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