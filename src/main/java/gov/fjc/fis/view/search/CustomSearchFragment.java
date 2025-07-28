package gov.fjc.fis.view.search;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import gov.fjc.fis.entity.*;
import gov.fjc.fis.event.FiscalYearChangeEvent;
import gov.fjc.fis.event.SearchGridSelectedItemsEvent;
import gov.fjc.fis.service.*;
import gov.fjc.fis.view.fileattachmentsearchfragment.FileAttachmentSearchFragment;
import io.jmix.core.LoadContext;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.JpqlCondition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.session.SessionData;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.multiselectcomboboxpicker.JmixMultiSelectComboBoxPicker;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.fragment.Fragment;
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
    @ViewComponent
    private CollectionLoader<FileAttachmentCategory> fileAttachmentCategoriesDl;

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
    @ViewComponent("searchTabSheet.subsetTab")
    private Tab searchTabSheetSubsetTab;
    @ViewComponent
    private VerticalLayout subFragmentSearchBox;
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
    private JmixButton showDiv9Btn;
    @ViewComponent
    private JmixButton showGroupBtn;
    @ViewComponent
    private JmixButton showSubsetBtn;
    @ViewComponent
    private JmixButton showBranchBtn;
    @ViewComponent
    private JmixButton showActivityBtn;
    @ViewComponent
    private JmixTabSheet searchTabSheet;
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
    private JmixMultiSelectComboBoxPicker<FileAttachmentCategory> fileCategorySearchField;
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
    private Class hostEntityClass;
    private String fundJoin;
    private String appropriationJoin;
    private String divisionJoin;
    private String activityJoin;
    private String categoryJoin;
    private String objectClassJoin;
    private String obligationJoin;
    private String branchJoin;
    private String groupJoin;
    private String fileCategoryJoin;
    private Fragment<VerticalLayout> subFragment;
    private List<Appropriation> fiscalYears;
    private List<Appropriation> searchYears;
    private String divisionCode;
    private String masterObjectClass;
    private boolean fjcFoundation;
    private Fund fjcFoundationFund;
    private int firstResult;
    private Integer tabIdx;
    private Map<String, Object> sessionSearchParams;
    private DataGrid<?> dataGrid;
    private List<Integer> subsetIds;
    private Group relatedGroup;
    private Branch relatedBranch;
    private Activity relatedActivity;
    private String subsetButtonId;

    /**
     * The hostDataContainer property must be explicitly set by the host invoking the fragment.
     * The hostDataContainer must have a dataLoader
     *
     * @param hostDataContainer dataContainer of host view
     */
    public void setHostDataContainer(CollectionContainer<?> hostDataContainer) {
        hostContainer = hostDataContainer;
        hostLoader = (CollectionLoader<?>) ((CollectionContainerImpl<?>) hostDataContainer).getLoader();
        if (hostLoader == null) {
            throw new IllegalStateException("hostLoader is null in SearchFragment");
        }
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

    public void setDataGrid(DataGrid<?> dataGrid) {
        this.dataGrid = dataGrid;
        searchTabSheetSubsetTab.setVisible(true);
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostAttach(final AttachEvent event) {
        if (hostContainer == null) {
            throw new IllegalStateException("hostContainer is null in SearchFragment");
        }
        fiscalYears = appropriationService.getBfyFilterField(sessionData);

        fjcFoundationFund = fundService.getFoundationFund();
    }

    @Subscribe(target = Target.HOST_CONTROLLER)
    protected void onHostReady(final View.ReadyEvent event) {
        configureHostEntity();
        if (fjcFoundation) {
            fundSearchField.setValue(fjcFoundationFund);
            fundSearchField.setReadOnly(true);
            // set visibility of division box?
        }
        restoreSearchParameters();
    }

    private void configureHostEntity() {
        hostEntityQuery = "SELECT e FROM ".concat(hostEntityName).concat(" e");
        switch (hostEntityName) {
            case "fis_Activity":
                hostEntityClass = Activity.class;
                hostLoader.setFetchPlan("activity-search-fetch-plan");
                hostEntityQuery += " ORDER BY e.division.appropriation.budgetFiscalYear, e.division.divisionCode, e.activityNumber";
                fundJoin = "JOIN {E}.fund f";
                appropriationJoin = "JOIN {E}.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.division dv";
                categoryJoin = "JOIN {E}.projections p JOIN p.objectClass obj JOIN obj.category cat";
                objectClassJoin = "JOIN {E}.projections p JOIN p.objectClass obj";
                branchJoin = "JOIN {E}.branch bch";
                groupJoin = "JOIN {E}.group grp";
                configureSubFragment(ActivitySearchFragment.class, "activitiesDc", "activitiesDl");
                showActivityBtn.setText("Show Generic Activity");
                break;
            case "fis_ActivityProjection":
                appropriationJoin = "JOIN {E}.activity act JOIN act.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.activity act JOIN act.division dv";
                break;
            case "fis_ActivityReimbursement":
                appropriationJoin = "JOIN {E}.activity act JOIN act.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.activity act JOIN act.division dv";
                break;
            case "fis_Obligation":
                hostEntityClass = Obligation.class;
                hostLoader.setFetchPlan("obligation-search-fetch-plan");
                hostEntityQuery += " ORDER BY e.activity.division.appropriation.budgetFiscalYear, e.activity.division.divisionCode, e.documentNumber, e.objectClass.budgetObjectClass";
                fundJoin = "JOIN {E}.activity act JOIN act.fund f";
                appropriationJoin = "JOIN {E}.activity act JOIN act.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.activity act JOIN act.division dv";
                activityJoin = "JOIN {E}.activity act";
                categoryJoin = "JOIN {E}.objectClass obj JOIN obj.category cat";
                objectClassJoin = "JOIN {E}.objectClass obj";
                branchJoin = "JOIN {E}.activity act JOIN act.branch bch";
                groupJoin = "JOIN {E}.activity act JOIN act.group grp";
                configureSubFragment(ObligationSearchFragment.class, "obligationsDc", "obligationsDl");
                break;
            case "fis_Invoice":
                hostLoader.setFetchPlan("invoice-search-fetch-plan");
                hostEntityQuery += " ORDER BY e.obligation.activity.division.appropriation.budgetFiscalYear, e.obligation.activity.division.divisionCode, e.obligation.documentNumber, e.obligation.objectClass.budgetObjectClass, e.invoiceNumber";
                fundJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.fund f";
                appropriationJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.division dv";
                categoryJoin = "JOIN {E}.obligation obl JOIN obl.objectClass obj JOIN obj.category cat";
                objectClassJoin = "JOIN {E}.obligation obl JOIN obl.objectClass obj";
                obligationJoin = "JOIN {E}.obligation obl";
                activityJoin = "JOIN {E}.obligation obl JOIN obl.activity act";
                branchJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.branch bch";
                groupJoin = "JOIN {E}.obligation obl JOIN obl.activity act JOIN act.group grp";
                configureSubFragment(InvoiceSearchFragment.class, "invoicesDc", "invoicesDl");
                break;
            case "fis_FundControlNotice":
                hostLoader.setFetchPlan("fundControlNotice-search-fetch-plan");
                hostEntityQuery += " ORDER BY e.obligation.activity.division.appropriation.budgetFiscalYear, e.obligation.activity.division.divisionCode, e.obligation.documentNumber, e.obligation.objectClass.budgetObjectClass, e.fcnDate";
                obligationJoin = "JOIN {E}.obligation obl";
                objectClassJoin = obligationJoin.concat(" JOIN obl.objectClass obj");
                categoryJoin = objectClassJoin.concat(" JOIN obj.category cat");
                activityJoin = obligationJoin.concat(" JOIN obl.activity act");
                fundJoin = activityJoin.concat(" JOIN act.fund f");
                divisionJoin = activityJoin.concat(" JOIN act.division dv");
                appropriationJoin = divisionJoin.concat(" JOIN dv.appropriation app");
                branchJoin = obligationJoin.concat(" JOIN obl.activity act JOIN act.branch bch");
                groupJoin = obligationJoin.concat(" JOIN obl.activity act JOIN act.group grp");
                configureSubFragment(FcnSearchFragment.class, "fundControlNoticesDc", "fundControlNoticesDl");
                break;
            case "fis_Division":
//                hostEntityQuery += " ORDER BY e.appropriation.budgetFiscalYear, e.divisionCode";
                appropriationJoin = "JOIN {E}.appropriation app";
                hostEntityQuery = "SELECT dv FROM fis_Division dv";
//                hostEntityQuery = "SELECT e FROM  e ORDER BY e.appropriation.budgetFiscalYear, e.divisionCode";
                fundJoin = "JOIN dv.fund f";
                break;
            case "fis_Branch":
                hostEntityQuery += " ORDER BY e.division.appropriation.budgetFiscalYear, e.division.divisionCode, e.branchCode";
                fundJoin = "JOIN {E}.division.fund f";
                appropriationJoin = "JOIN {E}.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.division d";
                break;
            case "fis_Group":
                hostEntityQuery += " ORDER BY e.division.appropriation.budgetFiscalYear, e.division.divisionCode, e.groupCode";
                fundJoin = "JOIN {E}.division.fund f";
                appropriationJoin = "JOIN {E}.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.division d";
                break;
            case "fis_Category":
                hostEntityQuery += " ORDER BY e.appropriation.budgetFiscalYear, e.masterObjectClass";
                fundJoin = null;
                appropriationJoin = "JOIN {E}.appropriation app";
//                hostEntityQuery = "SELECT cat FROM fis_Category cat ORDER BY cat.appropriation.budgetFiscalYear, cat.masterObjectClass";
                divisionSearchButtons.setVisible(false);
                break;
            case "fis_ObjectClass":
                hostLoader.setFetchPlan("objectClass-search-fetch-plan");
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
            case "fis_FileAttachment":
                hostEntityClass = FileAttachment.class;
                fundJoin = "JOIN {E}.activity.fund f";
                appropriationJoin = "JOIN {E}.activity.division dv JOIN dv.appropriation app";
                divisionJoin = "JOIN {E}.activity.division dv";
                activityJoin = "JOIN {E}.activity act";
                branchJoin = "JOIN {E}.activity act JOIN act.branch bch";
                groupJoin = "JOIN {E}.activity act JOIN act.group grp";
                fileCategoryJoin = "JOIN {E}.category fcat";
                configureSubFragment(FileAttachmentSearchFragment.class, "fileAttachmentsDc", "fileAttachmentsDl");
                break;
            default:
                throw new IllegalStateException(hostEntityName.concat(" has not been configured in CustomSearchFragment"));
        }
    }

    private void configureSubFragment(Class<? extends EntitySearchFragment> fragmentClass,
                                      String dataContainerId,
                                      String dataLoaderId) {

        // to this fragment, add container and loader with ids required by sub-fragment
//        FragmentData fragmentData = this.getFragmentData();
        this.getFragmentData().registerContainer(dataContainerId, hostContainer);
        this.getFragmentData().registerLoader(dataLoaderId, hostLoader);
//        FragmentUtils.setFragmentData(this, fragmentData);

        // create the sub-fragment, add components, get property filter conditions
        subFragment = fragments.create(this, fragmentClass);
        ((EntitySearchFragment) subFragment).addCategoryObjectClass(categorySearchField, objectClassSearchField);
        ((EntitySearchFragment) subFragment).addBranchGroup(branchSearchField, groupSearchField);
        ((EntitySearchFragment) subFragment).addFileCategory(fileCategorySearchField);

        // add sub fragment to this view and set visibility
        subFragmentSearchBox.add(subFragment);
        subFragmentSearchBox.setVisible(true);
        searchTabSheetCustomSearchTab.setVisible(true);
    }

    private void setBfyBtnCaption() {
        if (fiscalYears.size() == 1) {
            showBfyBtn.setText("Show all for " + fiscalYears.getFirst().getBudgetFiscalYear());
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
        showDiv9Btn.setVisible(divisionCodes.contains("9"));
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
//            hostLoader.removeParameter("divCodeFilterField");
        } else {
            divisionCode = event.getValue().getDivisionCode();
        }
        checkBranchAndGroup();
    }

    @Subscribe("categorySearchField")
    protected void onCategorySearchFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Category>, Category> event) {
        if (categorySearchField.getValue() == null) {
            masterObjectClass = null;
//            hostLoader.removeParameter("mocFilterField");
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
            clearCustomSearchParameters();
            String btnId = FragmentUtils.getComponentId(event.getComponent()).get();
            divisionCode = switch (btnId) {
                case "showDiv1Btn" -> "1";
                case "showDiv2Btn" -> "2";
                case "showDiv3Btn" -> "3";
                case "showDiv4Btn" -> "4";
                case "showDiv5Btn" -> "5";
                case "showDiv6Btn" -> "6";
                case "showDiv7Btn" -> "7";
                case "showDiv8Btn" -> "8";
                case "showDiv9Btn" -> "9";
                default -> null;
            };

            if (divisionCode != null) {
                hostLoader.setParameter("divCodeFilterField", divisionCode);
                performSearch();
            }
        }
    }

    @Subscribe(id = "showBfyBtn", subject = "clickListener")
    protected void onShowBfyBtnClick(final ClickEvent<JmixButton> event) {
        divisionCode = null;
        clearCustomSearchParameters();
        sessionSearchParams.clear(); // is this correct? probably
//        hostLoader.setParameter("bfyFilterField", searchYears);
        performSearch();
    }

    @Subscribe("searchTabSheet")
    protected void onSearchTabSheetSelectedChange(final JmixTabSheet.SelectedChangeEvent event) {
        tabIdx = event.getSource().getSelectedIndex();
        if (dataGrid != null) {
            dataGrid.setMultiSelect(tabIdx.equals(2));
        }
    }

    private void setSubsetLoaderParameters(String btnId) {
        clearCustomSearchParameters();
        removeSubsetLoaderParameters();

//        var selectedItems = dataGrid.getSelectedItems();

        switch (btnId) {
            case "showSubsetBtn":
                hostLoader.setParameter("idList", subsetIds);
                break;
            case "showGroupBtn":
                hostLoader.setParameter("relatedGroup", relatedGroup);
                break;
            case "showBranchBtn":
                hostLoader.setParameter("relatedBranch", relatedBranch);
                break;
            case "showActivityBtn":
                switch (hostEntityName) {
                    case "fis_Activity":
                        hostLoader.removeParameter("relatedActivity");
                        hostLoader.setParameter("genericActivityNumber", relatedActivity.getGenericActivityNumber());
                        hostLoader.setParameter("relatedActivityDivision", relatedActivity.getDivision());
                        break;
                    case "fis_Obligation":
                    case "fis_Invoice":
                    case "fis_FundControlNotice":
                    case "fis_FileAttachment":
                        hostLoader.setParameter("relatedActivity", relatedActivity);
                        break;
                }
                break;
        }
        performSearch();
        dataGrid.deselectAll();
    }

    @Subscribe("showSubsetAction")
    protected void onShowSubsetAction(final ActionPerformedEvent event) {
        var component = FragmentUtils.getComponentId(event.getComponent());
        if (component.isPresent()) {
            subsetButtonId = component.get();
            if (subsetButtonId.equals("showSubsetBtn")) {
                var selectedItems = dataGrid.getSelectedItems();
                subsetIds = null;
                switch (hostEntityName) {
                    case "fis_Activity" ->
                            subsetIds = ((Set<Activity>) selectedItems).stream().map(Activity::getId).toList();
                    case "fis_Obligation" ->
                            subsetIds = ((Set<Obligation>) selectedItems).stream().map(Obligation::getId).toList();
                    case "fis_Invoice" ->
                            subsetIds = ((Set<Invoice>) selectedItems).stream().map(Invoice::getId).toList();
                    case "fis_FundControlNotice" ->
                            subsetIds = ((Set<FundControlNotice>) selectedItems).stream().map(FundControlNotice::getId).toList();
                    case "fis_FileAttachment" ->
                            subsetIds = ((Set<FileAttachment>) selectedItems).stream().map(FileAttachment::getId).toList();
                }
            }
            setSubsetLoaderParameters(subsetButtonId);
        }
    }

    @Subscribe(id = "customSearchBtn", subject = "clickListener")
    protected void onCustomSearchBtnClick(final ClickEvent<JmixButton> event) {
//        hostLoader.setParameter("bfyFilterField", searchYears);
        setLoaderParameters();
        performSearch();
    }

    private void removeSubsetLoaderParameters() {
        hostLoader.removeParameter("idList");
        hostLoader.removeParameter("relatedGroup");
        hostLoader.removeParameter("relatedBranch");
        hostLoader.removeParameter("relatedActivity");
        hostLoader.removeParameter("relatedActivityDivision");
        hostLoader.removeParameter("genericActivityNumber");
    }

    private void setLoaderParameters() {
        removeSubsetLoaderParameters();

        if (fundSearchField.getValue() != null) {
            hostLoader.setParameter("fundFilterField", fundSearchField.getValue());
        } else {
            hostLoader.removeParameter("fundFilterField");
        }
        if (divisionSearchField.getValue() != null) {
            hostLoader.setParameter("divCodeFilterField", divisionSearchField.getValue().getDivisionCode());
        } else {
            hostLoader.removeParameter("divCodeFilterField");
        }
        if (categorySearchField.getValue() != null) {
            hostLoader.setParameter("mocFilterField", categorySearchField.getValue().getMasterObjectClass());
        } else {
            hostLoader.removeParameter("mocFilterField");
        }
        if (objectClassSearchField.getValue() != null) {
            hostLoader.setParameter("bocFilterField", objectClassSearchField.getValue().getBudgetObjectClass());
        } else {
            hostLoader.removeParameter("bocFilterField");
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
        if (!fileCategorySearchField.getValue().isEmpty()) {
            hostLoader.setParameter("fileCategoryFilterField", fileCategorySearchField.getValue());
        } else {
            hostLoader.removeParameter("fileCategoryFilterField");
        }
    }

    @Subscribe(id = "clearSearchBtn", subject = "clickListener")
    protected void onClearSearchBtnClick(final ClickEvent<JmixButton> event) {
        clearSearchFields();
    }

    private void clearSearchFields() {
        divisionSearchField.setValue(null);
        categorySearchField.setValue(null);
        objectClassSearchField.setValue(null);
        if (!fjcFoundation) {
            fundSearchField.setValue(null);
        }
        if (subFragment != null) {
            ((EntitySearchFragment) subFragment).clearPropertyFilters();
        }
    }

    private void clearCustomSearchParameters() {
        // remove query conditions from data loader
        Set<String> params = new HashSet<>(hostLoader.getParameters().keySet());
        params.forEach(hostLoader::removeParameter);

        clearSearchFields();

//        customFilters.forEach((key, value) -> value.setValue(null));
    }

    private void saveSearchParameters() {

        if (sessionSearchParams == null) {
            sessionSearchParams = new HashMap<>();
        }

        tabIdx = tabIdx == null ? 0 : tabIdx;

        sessionSearchParams.put("tab", tabIdx.toString());

        sessionSearchParams.put("bfyFilterField", fiscalYears);
        sessionSearchParams.put("quick_divisionCode", divisionCode);

//        sessionSearchParams.put("bfyFilterField", fiscalYears);
        sessionSearchParams.put("custom_fund", fundSearchField.getValue());
        sessionSearchParams.put("custom_division", divisionSearchField.getValue());
        sessionSearchParams.put("custom_category", categorySearchField.getValue());
        sessionSearchParams.put("custom_objectClass", objectClassSearchField.getValue());
        sessionSearchParams.put("custom_branch", branchSearchField.getValue());
        sessionSearchParams.put("custom_group", groupSearchField.getValue());
//        sessionSearchParams.put("custom_fileCategory", fileCategorySearchField.getValue());

        sessionSearchParams.put("subset_idList", subsetIds);
        sessionSearchParams.put("related_group", relatedGroup);
        sessionSearchParams.put("related_branch", relatedBranch);
        sessionSearchParams.put("related_activity", relatedActivity);
        sessionSearchParams.put("subset_button_id", subsetButtonId);

        if (subFragment != null) {
            List<PropertyFilter<?>> propertyFilters = ((EntitySearchFragment) subFragment).getPropertyFilters();
            for (PropertyFilter<?> filter : propertyFilters) {
                var name = filter.getProperty();
                if (name != null) {
                    String value = filter.getValue() != null ? filter.getValue().toString() : null;
                    Object filterValue = filter.getValue();
                    sessionSearchParams.put(name, filterValue);
                    if (filter.isOperationEditable()) {
                        sessionSearchParams.put(name.concat("_op"), filter.getOperation());
                    }
                }
            }
        }

        sessionData.setAttribute(hostEntityName.concat(".searchParams"), sessionSearchParams);
    }

    private void loadEntityComboBoxes() {
        setBfyBtnCaption();
        fundsDl.load();
        divisionsDl.load();
        categoriesDl.load();
        objectClassesDl.load();
        branchesDl.load();
        groupsDl.load();
        fileAttachmentCategoriesDl.load();
    }

    private void restoreSearchParameters() {

        // get entity search parameters
        sessionSearchParams = (Map<String, Object>) sessionData.getAttribute(hostEntityName.concat(".searchParams"));

        fiscalYears = appropriationService.getBfyFilterField(sessionData);

        if (sessionSearchParams == null) {
            loadEntityComboBoxes();
            searchYears = fiscalYears;
//            performSearch();
        } else {
            searchYears = (List<Appropriation>) sessionSearchParams.get("bfyFilterField");
            loadEntityComboBoxes();

            var tabParam = sessionSearchParams.get("tab");
            if (tabParam != null) {
                var tabIdx = Integer.parseInt((String) tabParam);
                searchTabSheet.setSelectedIndex(tabIdx);

                // quick search
                if (tabIdx == 0) {
                    divisionCode = (String) sessionSearchParams.get("quick_divisionCode");
                    if (divisionCode != null) {
                        hostLoader.setParameter("divCodeFilterField", divisionCode);
                    }
//                    performSearch();
                }

                // custom search
                if (tabIdx == 1) {
                    sessionSearchParams.remove("quick_divisionCode");// shouldn't be necessary

                    divisionSearchField.setValue((Division) sessionSearchParams.get("custom_division"));
                    fundSearchField.setValue((Fund) sessionSearchParams.get("custom_fund"));
                    categorySearchField.setValue((Category) sessionSearchParams.get("custom_category"));
                    objectClassSearchField.setValue((ObjectClass) sessionSearchParams.get("custom_objectClass"));
                    groupSearchField.setValue((Group) sessionSearchParams.get("custom_group"));
                    branchSearchField.setValue((Branch) sessionSearchParams.get("custom_branch"));
//                    fileCategorySearchField.setValue((FileAttachmentCategory) sessionSearchParams.get("custom_fileCategory"));
                    setLoaderParameters();

                    if (subFragment != null) {
                        ((EntitySearchFragment) subFragment).setPropertyFilters(sessionSearchParams);
                    }

//                    customSearchBtn.click();

                    // did the user change the fiscal year between the change and the restore?
                }

                if (tabIdx == 2) {
                    subsetIds = (List<Integer>) sessionSearchParams.get("subset_idList");
                    relatedGroup = (Group) sessionSearchParams.get("related_group");
                    relatedBranch = (Branch) sessionSearchParams.get("related_branch");
                    relatedActivity = (Activity) sessionSearchParams.get("related_activity");
                    subsetButtonId = (String) sessionSearchParams.get("subset_button_id");

                    setSubsetLoaderParameters(subsetButtonId);
                }

            }
        }

        performSearch();

        // changeFiscalYears() ?
        if (!searchYears.equals(fiscalYears)) {
            changeFiscalYears();
            searchYears = fiscalYears;
            loadEntityComboBoxes();
        }
    }

    private void performSearch() {
        List<Condition> customConditions = new ArrayList<>();
        List<Condition> subFragmentConditions;

        if (tabIdx != null && tabIdx.equals(2)) {
            customConditions.add(JpqlCondition.create("e.id in :idList", null).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("grp = :relatedGroup", groupJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("bch = :relatedBranch", branchJoin).skipNullOrEmpty());
            if (hostEntityName.equals("fis_Activity")) {
                customConditions.add(JpqlCondition.create("dv = :relatedActivityDivision", divisionJoin).skipNullOrEmpty());
                customConditions.add(JpqlCondition.create("e.activityNumber like :genericActivityNumber", null).skipNullOrEmpty());
            }
            if (hostEntityName.equals("fis_Obligation")
                    || hostEntityName.equals("fis_Invoice")
                    || hostEntityName.equals("fis_FundControlNotice")
                    || hostEntityName.equals("fis_FileAttachment")) {
                customConditions.add(JpqlCondition.create("act = :relatedActivity", activityJoin).skipNullOrEmpty());
            }
        } else {

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
            hostLoader.setParameter("bfyFilterField", searchYears);// this is an issue when idList is edited and returned after fy change

//        customConditions.add(JpqlCondition.createWithParameters("app in :bfyFilterField", appropriationJoin, Map.of("bfyFilterField", fiscalYears)));
            customConditions.add(JpqlCondition.create("app in :bfyFilterField", appropriationJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("dv.divisionCode = :divCodeFilterField", divisionJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("cat.masterObjectClass = :mocFilterField", categoryJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("obj.budgetObjectClass = :bocFilterField", objectClassJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("bch.branchCode = :branchCodeFilterField", branchJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("grp.groupCode = :groupCodeFilterField", groupJoin).skipNullOrEmpty());
            customConditions.add(JpqlCondition.create("fcat in :fileCategoryFilterField", fileCategoryJoin).skipNullOrEmpty());
//            customConditions.add(JpqlCondition.create("e.id in :idList", null).skipNullOrEmpty());
            if (hostEntityName.equals("fis_ActivityProjection")) {
                customConditions.add(JpqlCondition.create("e.amount <> 0", null).skipNullOrEmpty());
            }
            if (subFragment != null) {
                subFragmentConditions = ((EntitySearchFragment) subFragment).getPropertyFilterConditions();
                customConditions.addAll(subFragmentConditions);
            }
        }

//        ((EntitySearchFragment) subFragment).applyPropertyFilters();
        hostLoader.setQuery(hostEntityQuery);
        hostLoader.setCondition(LogicalCondition.and(customConditions.toArray(new Condition[0])));
        hostLoader.setFirstResult(firstResult);
        hostLoader.load();
        saveSearchParameters();
    }

    /**
     * Changes the Show BFY button caption after a Fiscal Year change event
     *
     * @param event custom event
     */
    @EventListener
    public void handleFiscalYearChangeEvent(FiscalYearChangeEvent event) {
        changeFiscalYears();
        searchYears = fiscalYears;
    }

    /**
     * if datagrid match, sets related items and button visibility
     *
     * @param event custom event that publishes grid and selection size
     */
    @EventListener
    public void handleSearchGridSelectedItemsEvent(SearchGridSelectedItemsEvent event) {
        if (event.getDataGrid().equals(dataGrid)) {
            var size = event.getSelectionSize();

            relatedGroup = null;
            relatedBranch = null;
            relatedActivity = null;

            if (size == 1) {
                var selectedItems = dataGrid.getSelectedItems();
                switch (hostEntityName) {
                    case "fis_Activity":
                        var activity = (Activity) selectedItems.stream().findFirst().get();
                        relatedGroup = activity.getGroup();
                        relatedBranch = activity.getBranch();
                        relatedActivity = activity;
                        break;
                    case "fis_Obligation":
                        var obligation = (Obligation) selectedItems.stream().findFirst().get();
                        relatedGroup = obligation.getActivity().getGroup();
                        relatedBranch = obligation.getActivity().getBranch();
                        relatedActivity = obligation.getActivity();
                        break;
                    case "fis_Invoice":
                        var invoice = (Invoice) selectedItems.stream().findFirst().get();
                        relatedGroup = invoice.getObligation().getActivity().getGroup();
                        relatedBranch = invoice.getObligation().getActivity().getBranch();
                        relatedActivity = invoice.getObligation().getActivity();
                        break;
                    case "fis_FundControlNotice":
                        var fcn = (FundControlNotice) selectedItems.stream().findFirst().get();
                        relatedGroup = fcn.getObligation().getActivity().getGroup();
                        relatedBranch = fcn.getObligation().getActivity().getBranch();
                        relatedActivity = fcn.getObligation().getActivity();
                        break;
                    case "fis_FileAttachment":
                        var fileAttachment = (FileAttachment) selectedItems.stream().findFirst().get();
                        relatedGroup = fileAttachment.getActivity().getGroup();
                        relatedBranch = fileAttachment.getActivity().getBranch();
                        relatedActivity = fileAttachment.getActivity();
                        break;
                }
            }

            showGroupBtn.setEnabled(size == 1 && relatedGroup != null);
            showBranchBtn.setEnabled(size == 1 && relatedBranch != null);
            showActivityBtn.setEnabled(size == 1 && relatedActivity != null);

            showSubsetBtn.setEnabled(size > 0);
            showSubsetBtn.setText("Show Subset (".concat(String.valueOf(size)).concat(")"));
        }
    }

    private void changeFiscalYears() {
        fiscalYears = appropriationService.getBfyFilterField(sessionData);
        setBfyBtnCaption();
        checkDivision();
        checkObjectClass();
        checkBranchAndGroup();
    }

    private void checkDivision() {
        divisionsDl.load();
        if (divisionSearchField.getValue() != null) {
            divisionSearchField.setValue(
                    divisionsDl.getContainer().getItems().stream()
                            .filter(div -> div.getDivisionCode().equals(divisionSearchField.getValue().getDivisionCode()))
                            .findFirst()
                            .orElse(null)
            );
        }
    }

    private void checkObjectClass() {
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
    }

    private void checkBranchAndGroup() {
        branchesDl.load();
        if (branchSearchField.getValue() != null) {
            branchSearchField.setValue(
                    branchesDl.getContainer().getItems().stream()
                            .filter(bch -> bch.getBranchCode().equals(branchSearchField.getValue().getBranchCode()))
                            .findFirst()
                            .orElse(null)
            );
        }
        groupsDl.load();
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