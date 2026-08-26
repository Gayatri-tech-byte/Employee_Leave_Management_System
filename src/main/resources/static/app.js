/**
 * ─────────────────────────────────────────────────────────────
 *  Employee Leave Management System – Authentication & Portals
 * ─────────────────────────────────────────────────────────────
 */

// ── Configuration ────────────────────────────────────────────
const API_BASE_URL = window.location.origin.includes('http') 
    ? window.location.origin 
    : 'http://localhost:8080';

// ── State ────────────────────────────────────────────────────
let state = {
    currentUser: null, // { id, name, email, department, role }
    employees: [],
    leaves: [],
    adminActiveTab: 'admin-leaves-tab',
    adminStatusFilter: 'ALL',
    adminTypeFilter: 'ALL',
    adminSearchQuery: '',
    adminEmpSearchQuery: ''
};

// ── DOM Elements ─────────────────────────────────────────────
const elements = {
    // Screens
    viewAuth: document.getElementById('view-auth'),
    viewAdminPortal: document.getElementById('view-admin-portal'),
    viewEmployeePortal: document.getElementById('view-employee-portal'),

    // Auth Screen
    authForm: document.getElementById('auth-form'),
    authEmail: document.getElementById('auth-email'),
    authPassword: document.getElementById('auth-password'),
    authEmailError: document.getElementById('auth-email-error'),
    authPasswordError: document.getElementById('auth-password-error'),
    btnTogglePassword: document.getElementById('btn-toggle-password'),
    togglePwdIcon: document.getElementById('toggle-pwd-icon'),
    btnFillAdmin: document.getElementById('btn-fill-admin'),
    btnFillEmployee: document.getElementById('btn-fill-employee'),

    // Admin Portal
    adminUserName: document.getElementById('admin-user-name'),
    adminPendingBadge: document.getElementById('admin-pending-badge'),
    btnAdminLogout: document.getElementById('btn-admin-logout'),
    btnAdminRefresh: document.getElementById('btn-admin-refresh'),
    adminNavBtns: document.querySelectorAll('.nav-item[data-portal="admin"]'),
    adminTabPanels: document.querySelectorAll('#view-admin-portal .tab-panel'),
    adminPageHeading: document.getElementById('admin-page-heading'),
    adminPageSubheading: document.getElementById('admin-page-subheading'),

    // Admin Stats
    adminStatTotalEmp: document.getElementById('admin-stat-total-emp'),
    adminStatTotalLeaves: document.getElementById('admin-stat-total-leaves'),
    adminStatPendingLeaves: document.getElementById('admin-stat-pending-leaves'),
    adminStatApprovedLeaves: document.getElementById('admin-stat-approved-leaves'),

    // Admin Tables & Filters
    adminLeavesTableBody: document.getElementById('admin-leaves-table-body'),
    adminEmployeesTableBody: document.getElementById('admin-employees-table-body'),
    adminLeaveSearch: document.getElementById('admin-leave-search'),
    adminStatusFilter: document.getElementById('admin-status-filter'),
    adminTypeFilter: document.getElementById('admin-type-filter'),
    adminEmpSearch: document.getElementById('admin-emp-search'),

    // Admin Forms
    adminEmployeeForm: document.getElementById('admin-employee-form'),
    btnOpenAddEmpModal: document.getElementById('btn-open-add-emp-modal'),
    btnOpenAdminLeaveModal: document.getElementById('btn-open-admin-leave-modal'),

    // Admin Visual Insights
    adminResultQ1: document.getElementById('admin-result-q1'),
    adminResultQ2: document.getElementById('admin-result-q2'),
    adminResultQ3: document.getElementById('admin-result-q3'),
    adminResultQ4: document.getElementById('admin-result-q4'),
    adminResultQ5: document.getElementById('admin-result-q5'),

    // Employee Portal
    empUserName: document.getElementById('emp-user-name'),
    empUserDept: document.getElementById('emp-user-dept'),
    empWelcomeName: document.getElementById('emp-welcome-name'),
    btnEmpLogout: document.getElementById('btn-emp-logout'),
    btnEmpRefresh: document.getElementById('btn-emp-refresh'),
    btnEmpOpenLeaveModal: document.getElementById('btn-emp-open-leave-modal'),

    // Employee Stats & Table
    empStatDaysTaken: document.getElementById('emp-stat-days-taken'),
    empStatPending: document.getElementById('emp-stat-pending'),
    empStatApproved: document.getElementById('emp-stat-approved'),
    empStatTotalApps: document.getElementById('emp-stat-total-apps'),
    empLeavesTableBody: document.getElementById('emp-leaves-table-body'),

    // Modals
    leaveModal: document.getElementById('leave-modal'),
    employeeModal: document.getElementById('employee-modal'),
    btnCloseLeaveModal: document.getElementById('btn-close-leave-modal'),
    btnCancelLeaveModal: document.getElementById('btn-cancel-leave-modal'),
    btnCloseEmpModal: document.getElementById('btn-close-emp-modal'),
    btnCancelEmpModal: document.getElementById('btn-cancel-emp-modal'),

    // Leave Form
    leaveForm: document.getElementById('leave-form'),
    leaveEmpSelectWrap: document.getElementById('leave-emp-select-wrap'),
    leaveEmpLockedPill: document.getElementById('leave-emp-locked-pill'),
    lockedEmpName: document.getElementById('locked-emp-name'),
    leaveEmpId: document.getElementById('leave-emp-id'),
    leaveType: document.getElementById('leave-type'),
    leaveStartDate: document.getElementById('leave-start-date'),
    leaveEndDate: document.getElementById('leave-end-date'),
    leaveReason: document.getElementById('leave-reason'),
    calcDaysDisplay: document.getElementById('calc-days-display'),

    // Toast Container
    toastContainer: document.getElementById('toast-container')
};

// ── Initialization ───────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    setupDateValidation();
    checkSavedSession();
});

// ── Event Listeners ──────────────────────────────────────────
function setupEventListeners() {
    // Auth Handlers
    elements.authForm.addEventListener('submit', handleLogin);
    
    // Toggle Password Visibility
    if (elements.btnTogglePassword) {
        elements.btnTogglePassword.addEventListener('click', () => {
            const isPassword = elements.authPassword.type === 'password';
            elements.authPassword.type = isPassword ? 'text' : 'password';
            elements.togglePwdIcon.className = isPassword ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye';
        });
    }

    // Quick Fill Demo Chips
    if (elements.btnFillAdmin) {
        elements.btnFillAdmin.addEventListener('click', () => {
            elements.authEmail.value = 'admin@leavehub.com';
            elements.authPassword.value = 'admin123';
            clearAuthErrors();
            showToast('Filled HR Administrator credentials (admin@leavehub.com)', 'info');
        });
    }

    if (elements.btnFillEmployee) {
        elements.btnFillEmployee.addEventListener('click', () => {
            elements.authEmail.value = 'alice.johnson@example.com';
            elements.authPassword.value = 'password123';
            clearAuthErrors();
            showToast('Filled Employee credentials (alice.johnson@example.com)', 'info');
        });
    }

    // Logout Handlers
    elements.btnAdminLogout.addEventListener('click', handleLogout);
    elements.btnEmpLogout.addEventListener('click', handleLogout);

    // Refresh Handlers
    elements.btnAdminRefresh.addEventListener('click', () => refreshData(elements.btnAdminRefresh));
    elements.btnEmpRefresh.addEventListener('click', () => refreshData(elements.btnEmpRefresh));

    // Admin Navigation
    elements.adminNavBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.getAttribute('data-tab');
            switchAdminTab(tabId);
        });
    });

    // Admin Filters
    elements.adminLeaveSearch.addEventListener('input', (e) => {
        state.adminSearchQuery = e.target.value.toLowerCase().trim();
        renderAdminLeavesTable();
    });

    elements.adminStatusFilter.addEventListener('change', (e) => {
        state.adminStatusFilter = e.target.value;
        renderAdminLeavesTable();
    });

    elements.adminTypeFilter.addEventListener('change', (e) => {
        state.adminTypeFilter = e.target.value;
        renderAdminLeavesTable();
    });

    elements.adminEmpSearch.addEventListener('input', (e) => {
        state.adminEmpSearchQuery = e.target.value.toLowerCase().trim();
        renderAdminEmployeesTable();
    });

    // Modals Open
    if (elements.btnOpenAddEmpModal && elements.employeeModal) {
        elements.btnOpenAddEmpModal.addEventListener('click', () => openModal(elements.employeeModal));
    }
    if (elements.btnOpenAdminLeaveModal) {
        elements.btnOpenAdminLeaveModal.addEventListener('click', () => openLeaveModal(true));
    }
    if (elements.btnEmpOpenLeaveModal) {
        elements.btnEmpOpenLeaveModal.addEventListener('click', () => openLeaveModal(false));
    }

    // Modals Close
    if (elements.btnCloseLeaveModal) elements.btnCloseLeaveModal.addEventListener('click', () => closeModal(elements.leaveModal));
    if (elements.btnCancelLeaveModal) elements.btnCancelLeaveModal.addEventListener('click', () => closeModal(elements.leaveModal));
    if (elements.btnCloseEmpModal && elements.employeeModal) elements.btnCloseEmpModal.addEventListener('click', () => closeModal(elements.employeeModal));
    if (elements.btnCancelEmpModal && elements.employeeModal) elements.btnCancelEmpModal.addEventListener('click', () => closeModal(elements.employeeModal));

    [elements.leaveModal, elements.employeeModal].filter(Boolean).forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) closeModal(modal);
        });
    });

    // Form Submissions
    if (elements.adminEmployeeForm) {
        elements.adminEmployeeForm.addEventListener('submit', (e) => handleEmployeeCreate(e, false));
    }
    const modalEmpForm = document.getElementById('modal-employee-form');
    if (modalEmpForm) modalEmpForm.addEventListener('submit', (e) => handleEmployeeCreate(e, true));
    if (elements.leaveForm) elements.leaveForm.addEventListener('submit', handleLeaveSubmit);
}

// ── Authentication & Session Management ──────────────────────
function checkSavedSession() {
    try {
        const saved = sessionStorage.getItem('leavehub_user');
        if (saved) {
            const user = JSON.parse(saved);
            routeToPortal(user);
            return;
        }
    } catch (e) {
        console.warn('Session parse error:', e);
    }
    showAuthScreen();
}

function clearAuthErrors() {
    elements.authEmailError.textContent = '';
    elements.authPasswordError.textContent = '';
}

async function handleLogin(e) {
    e.preventDefault();
    clearAuthErrors();

    const email = elements.authEmail.value.trim();
    const password = elements.authPassword.value.trim();

    let isValid = true;
    if (!email) {
        elements.authEmailError.textContent = 'Please enter your work email address';
        isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        elements.authEmailError.textContent = 'Please enter a valid email format';
        isValid = false;
    }

    if (!password) {
        elements.authPasswordError.textContent = 'Please enter your password';
        isValid = false;
    }

    if (!isValid) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await res.json();

        if (!res.ok) {
            const errorMsg = data.message || 'Invalid email or password';
            if (errorMsg.toLowerCase().includes('password')) {
                elements.authPasswordError.textContent = errorMsg;
            } else {
                elements.authEmailError.textContent = errorMsg;
            }
            showToast(errorMsg, 'error');
            return;
        }

        sessionStorage.setItem('leavehub_user', JSON.stringify(data));
        showToast(data.message || `Welcome, ${data.name}!`, 'success');
        routeToPortal(data);

    } catch (err) {
        console.error('Login error:', err);
        // Fallback for demo when backend is offline
        if (email.includes('admin') || email.includes('hr')) {
            const adminUser = { id: 0, name: 'HR Administrator', email, department: 'Human Resources', role: 'ADMIN' };
            sessionStorage.setItem('leavehub_user', JSON.stringify(adminUser));
            routeToPortal(adminUser);
        } else {
            const empUser = { id: 1, name: 'Alice Johnson', email, department: 'Engineering', role: 'EMPLOYEE' };
            sessionStorage.setItem('leavehub_user', JSON.stringify(empUser));
            routeToPortal(empUser);
        }
    }
}

function routeToPortal(user) {
    if (!user) return;
    state.currentUser = user;
    if (elements.viewAuth) elements.viewAuth.style.display = 'none';

    if (user.role === 'ADMIN') {
        if (elements.viewAdminPortal) elements.viewAdminPortal.style.display = 'block';
        if (elements.viewEmployeePortal) elements.viewEmployeePortal.style.display = 'none';
        const adminNameElem = document.getElementById('admin-user-name');
        if (adminNameElem) adminNameElem.textContent = user.name || 'HR Administrator';
        loadAdminData();
    } else {
        if (elements.viewAdminPortal) elements.viewAdminPortal.style.display = 'none';
        if (elements.viewEmployeePortal) elements.viewEmployeePortal.style.display = 'block';
        if (elements.empUserName) elements.empUserName.textContent = user.name || 'Staff Member';
        if (elements.empUserDept) elements.empUserDept.textContent = `${user.department || 'Staff'} Department`;
        if (elements.empWelcomeName) elements.empWelcomeName.textContent = (user.name || '').split(' ')[0] || user.name || 'Staff';
        loadEmployeeData();
    }
}

function handleLogout() {
    sessionStorage.removeItem('leavehub_user');
    state.currentUser = null;
    showToast('You have been signed out.', 'info');
    showAuthScreen();
}

function showAuthScreen() {
    elements.viewAuth.style.display = 'flex';
    elements.viewAdminPortal.style.display = 'none';
    elements.viewEmployeePortal.style.display = 'none';
    elements.authEmail.value = '';
    elements.authPassword.value = '';
    clearAuthErrors();
}

// ── Data Loaders ─────────────────────────────────────────────
async function loadAdminData() {
    await fetchEmployees();
    await fetchLeaves();
    updateAdminStats();
    renderAdminLeavesTable();
    renderAdminEmployeesTable();
    renderAdminAnalytics();
}

async function loadEmployeeData() {
    await fetchLeaves();
    updateEmployeeStats();
    renderEmployeeLeavesTable();
}

async function refreshData(btnEl) {
    if (btnEl) btnEl.classList.add('rotating');
    if (state.currentUser.role === 'ADMIN') {
        await loadAdminData();
    } else {
        await loadEmployeeData();
    }
    showToast('Dashboard refreshed!', 'info');
    if (btnEl) setTimeout(() => btnEl.classList.remove('rotating'), 500);
}

async function fetchEmployees() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/employees`);
        if (res.ok) {
            state.employees = await res.json();
            populateEmployeeDropdown();
        }
    } catch (err) {
        console.error('Fetch employees error:', err);
    }
}

async function fetchLeaves() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/leaves`);
        if (res.ok) {
            state.leaves = await res.json();
        }
    } catch (err) {
        console.error('Fetch leaves error:', err);
    }
}

// ── Admin Portal Features ────────────────────────────────────
function switchAdminTab(tabId) {
    state.adminActiveTab = tabId;

    elements.adminNavBtns.forEach(btn => {
        if (btn.getAttribute('data-tab') === tabId) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    elements.adminTabPanels.forEach(panel => {
        if (panel.id === tabId) {
            panel.classList.add('active');
        } else {
            panel.classList.remove('active');
        }
    });

    if (tabId === 'admin-leaves-tab') {
        elements.adminPageHeading.textContent = 'Leave Requests Management';
        elements.adminPageSubheading.textContent = 'Review, approve, or reject employee leave applications';
    } else if (tabId === 'admin-employees-tab') {
        elements.adminPageHeading.textContent = 'Staff & Employee Directory';
        elements.adminPageSubheading.textContent = 'Register staff accounts and review organization directory';
    } else if (tabId === 'admin-analytics-tab') {
        elements.adminPageHeading.textContent = 'Enterprise Leave Insights & Visual KPI Analytics';
        elements.adminPageSubheading.textContent = 'Real-time visual monitoring of leave consumption, approval queues, employee attendance, and category breakdown';
        renderAdminAnalytics();
    }
}

function updateAdminStats() {
    const totalEmp = state.employees.length;
    const totalLeaves = state.leaves.length;
    const pending = state.leaves.filter(l => l.status === 'PENDING').length;
    const approved = state.leaves.filter(l => l.status === 'APPROVED').length;

    elements.adminStatTotalEmp.textContent = totalEmp;
    elements.adminStatTotalLeaves.textContent = totalLeaves;
    elements.adminStatPendingLeaves.textContent = pending;
    elements.adminStatApprovedLeaves.textContent = approved;
    elements.adminPendingBadge.textContent = pending;
}

function renderAdminLeavesTable() {
    let filtered = [...state.leaves];

    if (state.adminStatusFilter !== 'ALL') {
        filtered = filtered.filter(l => l.status === state.adminStatusFilter);
    }

    if (state.adminTypeFilter !== 'ALL') {
        filtered = filtered.filter(l => l.leaveType === state.adminTypeFilter);
    }

    if (state.adminSearchQuery) {
        filtered = filtered.filter(l => 
            (l.employeeName && l.employeeName.toLowerCase().includes(state.adminSearchQuery)) ||
            (l.employeeDepartment && l.employeeDepartment.toLowerCase().includes(state.adminSearchQuery)) ||
            (l.reason && l.reason.toLowerCase().includes(state.adminSearchQuery))
        );
    }

    if (filtered.length === 0) {
        elements.adminLeavesTableBody.innerHTML = `
            <tr><td colspan="9" class="empty-cell">No leave requests found matching filters.</td></tr>
        `;
        return;
    }

    elements.adminLeavesTableBody.innerHTML = filtered.map(l => {
        const canApprove = l.status === 'PENDING';
        const canReject = l.status === 'PENDING';
        const canCancel = l.status === 'PENDING' || l.status === 'APPROVED';

        return `
            <tr>
                <td><strong>#${l.id}</strong></td>
                <td>
                    <div style="font-weight: 700;">${escapeHtml(l.employeeName || 'Staff Member')}</div>
                    <div style="font-size: 11px; color: var(--text-muted);">Emp ID: #${l.employeeId}</div>
                </td>
                <td><span style="color: var(--text-secondary);">${escapeHtml(l.employeeDepartment || '-')}</span></td>
                <td><span class="type-pill type-${l.leaveType}">${l.leaveType}</span></td>
                <td><div style="font-weight: 600;">${formatDate(l.startDate)} &rarr; ${formatDate(l.endDate)}</div></td>
                <td><span class="days-pill">${l.leaveDays} Day${l.leaveDays > 1 ? 's' : ''}</span></td>
                <td><span style="color: var(--text-secondary); font-size: 12px;">${escapeHtml(l.reason || 'None')}</span></td>
                <td><span class="status-pill status-${l.status}">${l.status}</span></td>
                <td>
                    <div class="actions-cell">
                        ${canApprove ? `<button class="btn btn-sm btn-success" onclick="adminAction(${l.id}, 'approve')" title="Approve Request"><i class="fa-solid fa-check"></i> Approve</button>` : ''}
                        ${canReject ? `<button class="btn btn-sm btn-danger" onclick="adminAction(${l.id}, 'reject')" title="Reject Request"><i class="fa-solid fa-xmark"></i> Reject</button>` : ''}
                        ${canCancel ? `<button class="btn btn-sm btn-neutral" onclick="adminAction(${l.id}, 'cancel')" title="Cancel Request"><i class="fa-solid fa-ban"></i></button>` : ''}
                        ${!canApprove && !canReject && !canCancel ? `<span style="font-size: 11px; color: var(--text-muted);">Resolved</span>` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

async function adminAction(leaveId, action) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/leaves/${leaveId}/${action}`, {
            method: 'PUT'
        });

        const data = await res.json();

        if (!res.ok) {
            showToast(data.message || `Failed to ${action} leave`, 'error');
            return;
        }

        const actionText = action === 'approve' ? 'Approved' : action === 'reject' ? 'Rejected' : 'Cancelled';
        showToast(`Leave Request #${leaveId} has been ${actionText}!`, 'success');
        await loadAdminData();

    } catch (err) {
        console.error(err);
        showToast(`Network error while trying to ${action} leave`, 'error');
    }
}

function renderAdminEmployeesTable() {
    let filtered = [...state.employees];

    if (state.adminEmpSearchQuery) {
        filtered = filtered.filter(e => 
            e.name.toLowerCase().includes(state.adminEmpSearchQuery) ||
            e.email.toLowerCase().includes(state.adminEmpSearchQuery) ||
            e.department.toLowerCase().includes(state.adminEmpSearchQuery) ||
            (e.role && e.role.toLowerCase().includes(state.adminEmpSearchQuery))
        );
    }

    if (filtered.length === 0) {
        elements.adminEmployeesTableBody.innerHTML = `<tr><td colspan="5" class="empty-cell">No staff records found.</td></tr>`;
        return;
    }

    elements.adminEmployeesTableBody.innerHTML = filtered.map(e => `
        <tr>
            <td><strong>#${e.id}</strong></td>
            <td><strong>${escapeHtml(e.name)}</strong></td>
            <td><span class="role-pill role-${e.role || 'EMPLOYEE'}">${e.role || 'EMPLOYEE'}</span></td>
            <td><code style="color: #4f46e5;">${escapeHtml(e.email)}</code></td>
            <td><span style="color: var(--text-secondary);">${escapeHtml(e.department)}</span></td>
        </tr>
    `).join('');
}

// ── Visual Insights Renderer (No Raw SQL Snippets) ───────────
function renderAdminAnalytics() {
    if (!state.employees || !state.leaves) return;

    // Visual Insight 1: Employees with > 5 leave days (Visual Meter Bars)
    const empTotals = {};
    state.leaves.forEach(l => { empTotals[l.employeeId] = (empTotals[l.employeeId] || 0) + l.leaveDays; });
    const q1List = state.employees
        .map(e => ({ ...e, totalDays: empTotals[e.id] || 0 }))
        .filter(e => e.totalDays > 5)
        .sort((a, b) => b.totalDays - a.totalDays);

    if (q1List.length > 0) {
        const maxDays = Math.max(...q1List.map(x => x.totalDays), 10);
        elements.adminResultQ1.innerHTML = q1List.map(e => {
            const pct = Math.min(100, Math.round((e.totalDays / maxDays) * 100));
            return `
                <div class="visual-progress-item">
                    <div class="visual-progress-header">
                        <div><strong>${escapeHtml(e.name)}</strong> <span style="font-size: 11px; color: var(--text-muted);">(${escapeHtml(e.department)})</span></div>
                        <div style="font-weight: 800; color: #b45309;"><i class="fa-solid fa-calendar-xmark"></i> ${e.totalDays} Days</div>
                    </div>
                    <div class="visual-bar-track">
                        <div class="visual-bar-fill fill-amber" style="width: ${pct}%;"></div>
                    </div>
                </div>
            `;
        }).join('');
    } else {
        elements.adminResultQ1.innerHTML = `<div class="empty-state-sm">No employees have exceeded 5 leave days.</div>`;
    }

    // Visual Insight 2: Staff Leave Usage Distribution (Visual Progress Breakdown)
    const empReqCount = {};
    state.leaves.forEach(l => { empReqCount[l.employeeId] = (empReqCount[l.employeeId] || 0) + 1; });
    const q2List = state.employees
        .map(e => ({ ...e, totalDays: empTotals[e.id] || 0, requests: empReqCount[e.id] || 0 }))
        .sort((a, b) => b.totalDays - a.totalDays);

    if (q2List.length > 0) {
        const topMax = Math.max(...q2List.map(x => x.totalDays), 1);
        elements.adminResultQ2.innerHTML = q2List.map(e => {
            const pct = Math.max(8, Math.round((e.totalDays / topMax) * 100));
            return `
                <div class="visual-progress-item">
                    <div class="visual-progress-header">
                        <div><strong>${escapeHtml(e.name)}</strong> <span style="font-size: 11px; color: var(--text-muted);">${e.requests} reqs</span></div>
                        <div style="font-weight: 700; color: #0284c7;">${e.totalDays} Days Taken</div>
                    </div>
                    <div class="visual-bar-track">
                        <div class="visual-bar-fill fill-blue" style="width: ${pct}%;"></div>
                    </div>
                </div>
            `;
        }).join('');
    } else {
        elements.adminResultQ2.innerHTML = `<div class="empty-state-sm">No staff records available.</div>`;
    }

    // Visual Insight 3: Pending Approvals Priority Queue
    const q3List = state.leaves.filter(l => l.status === 'PENDING');
    if (q3List.length > 0) {
        elements.adminResultQ3.innerHTML = q3List.map(l => `
            <div class="queue-card">
                <div class="queue-info">
                    <div class="queue-name">
                        ${escapeHtml(l.employeeName)}
                        <span class="type-pill type-${l.leaveType}" style="margin-left: 6px;">${l.leaveType}</span>
                    </div>
                    <div class="queue-meta">
                        ${formatDate(l.startDate)} &rarr; ${formatDate(l.endDate)} (<strong>${l.leaveDays} Days</strong>)
                    </div>
                </div>
                <div class="queue-actions">
                    <button class="btn btn-sm btn-success" onclick="adminAction(${l.id}, 'approve')" title="Approve"><i class="fa-solid fa-check"></i></button>
                    <button class="btn btn-sm btn-danger" onclick="adminAction(${l.id}, 'reject')" title="Reject"><i class="fa-solid fa-xmark"></i></button>
                </div>
            </div>
        `).join('');
    } else {
        elements.adminResultQ3.innerHTML = `<div class="empty-state-sm"><i class="fa-solid fa-circle-check" style="color: #10b981; font-size: 16px; margin-bottom: 4px; display: block;"></i> All pending leave requests are resolved!</div>`;
    }

    // Visual Insight 4: Zero-Leave Attendance Champions
    const empIdsWithLeaves = new Set(state.leaves.map(l => l.employeeId));
    const q4List = state.employees.filter(e => !empIdsWithLeaves.has(e.id));
    if (q4List.length > 0) {
        elements.adminResultQ4.innerHTML = `
            <div class="champions-grid">
                ${q4List.map(e => `
                    <div class="champion-card">
                        <div class="champion-icon"><i class="fa-solid fa-star"></i></div>
                        <div>
                            <div style="font-weight: 700; font-size: 13px;">${escapeHtml(e.name)}</div>
                            <div style="font-size: 11px; color: var(--text-muted);">${escapeHtml(e.department)}</div>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    } else {
        elements.adminResultQ4.innerHTML = `<div class="empty-state-sm">All registered staff members have submitted leave requests.</div>`;
    }

    // Visual Insight 5: Leave Category Popularity & Category Share
    const typeCounts = { CL: 0, SL: 0, EL: 0 };
    state.leaves.forEach(l => { if (typeCounts[l.leaveType] !== undefined) typeCounts[l.leaveType]++; });
    const totalCount = state.leaves.length || 1;

    const clPct = Math.round((typeCounts.CL / totalCount) * 100);
    const slPct = Math.round((typeCounts.SL / totalCount) * 100);
    const elPct = Math.round((typeCounts.EL / totalCount) * 100);

    let topType = 'CL';
    if (typeCounts.SL > typeCounts.CL && typeCounts.SL >= typeCounts.EL) topType = 'SL';
    if (typeCounts.EL > typeCounts.CL && typeCounts.EL > typeCounts.SL) topType = 'EL';

    elements.adminResultQ5.innerHTML = `
        <div class="category-shares-container">
            <div class="category-stat-card" style="border-left: 4px solid #0284c7;">
                <div class="category-stat-top">
                    <span class="type-pill type-CL">Casual Leave (CL)</span>
                    <span class="category-stat-pct">${clPct}% Share</span>
                </div>
                <div class="category-stat-count">${typeCounts.CL} <span style="font-size: 12px; font-weight: 600; color: var(--text-muted);">requests</span></div>
                <div class="visual-bar-track"><div class="visual-bar-fill fill-blue" style="width: ${clPct}%;"></div></div>
            </div>

            <div class="category-stat-card" style="border-left: 4px solid #d97706;">
                <div class="category-stat-top">
                    <span class="type-pill type-SL">Sick Leave (SL)</span>
                    <span class="category-stat-pct">${slPct}% Share</span>
                </div>
                <div class="category-stat-count">${typeCounts.SL} <span style="font-size: 12px; font-weight: 600; color: var(--text-muted);">requests</span></div>
                <div class="visual-bar-track"><div class="visual-bar-fill fill-amber" style="width: ${slPct}%;"></div></div>
            </div>

            <div class="category-stat-card" style="border-left: 4px solid #7e22ce;">
                <div class="category-stat-top">
                    <span class="type-pill type-EL">Earned Leave (EL)</span>
                    <span class="category-stat-pct">${elPct}% Share</span>
                </div>
                <div class="category-stat-count">${typeCounts.EL} <span style="font-size: 12px; font-weight: 600; color: var(--text-muted);">requests</span></div>
                <div class="visual-bar-track"><div class="visual-bar-fill fill-purple" style="width: ${elPct}%;"></div></div>
            </div>
        </div>
    `;
}

// ── Employee Portal Features ─────────────────────────────────
function updateEmployeeStats() {
    if (!state.currentUser) return;
    const empId = state.currentUser.id;
    const myLeaves = state.leaves.filter(l => l.employeeId === empId);

    const approvedDays = myLeaves
        .filter(l => l.status === 'APPROVED')
        .reduce((sum, l) => sum + l.leaveDays, 0);

    const pending = myLeaves.filter(l => l.status === 'PENDING').length;
    const approved = myLeaves.filter(l => l.status === 'APPROVED').length;

    elements.empStatDaysTaken.textContent = `${approvedDays} Days`;
    elements.empStatPending.textContent = pending;
    elements.empStatApproved.textContent = approved;
    elements.empStatTotalApps.textContent = myLeaves.length;
}

function renderEmployeeLeavesTable() {
    if (!state.currentUser) return;
    const empId = state.currentUser.id;
    const myLeaves = state.leaves.filter(l => l.employeeId === empId);

    if (myLeaves.length === 0) {
        elements.empLeavesTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">
                    <i class="fa-solid fa-calendar-plus" style="font-size: 24px; color: var(--primary); margin-bottom: 8px; display: block;"></i>
                    You haven't submitted any leave applications yet. Click "Send Leave Request" above to apply!
                </td>
            </tr>
        `;
        return;
    }

    elements.empLeavesTableBody.innerHTML = myLeaves.map(l => {
        const isPending = l.status === 'PENDING';
        const isApproved = l.status === 'APPROVED';

        let actionHtml = '';
        if (isPending) {
            actionHtml = `<button class="btn btn-sm btn-logout" onclick="employeeCancelLeave(${l.id})" title="Cancel this leave request"><i class="fa-solid fa-ban"></i> Cancel</button>`;
        } else if (isApproved) {
            actionHtml = `<button class="btn btn-sm btn-neutral" disabled style="opacity: 0.45; cursor: not-allowed;" title="Approved leaves cannot be cancelled"><i class="fa-solid fa-ban"></i> Cancel</button>`;
        } else {
            actionHtml = `<span style="font-size: 11.5px; color: var(--text-muted);">Resolved</span>`;
        }

        return `
            <tr>
                <td><strong>#${l.id}</strong></td>
                <td><span class="type-pill type-${l.leaveType}">${l.leaveType}</span></td>
                <td><div style="font-weight: 600;">${formatDate(l.startDate)} &rarr; ${formatDate(l.endDate)}</div></td>
                <td><span class="days-pill">${l.leaveDays} Day${l.leaveDays > 1 ? 's' : ''}</span></td>
                <td><span style="color: var(--text-secondary); font-size: 12.5px;">${escapeHtml(l.reason || 'None provided')}</span></td>
                <td><span class="status-pill status-${l.status}">${l.status}</span></td>
                <td>${actionHtml}</td>
            </tr>
        `;
    }).join('');
}

async function employeeCancelLeave(leaveId) {
    const targetLeave = state.leaves.find(l => l.id === leaveId);
    if (targetLeave && targetLeave.status === 'APPROVED') {
        showToast('Approved leave applications cannot be cancelled.', 'error');
        return;
    }

    if (!confirm('Are you sure you want to cancel this leave application?')) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/leaves/${leaveId}/cancel`, {
            method: 'PUT'
        });

        const data = await res.json();

        if (!res.ok) {
            showToast(data.message || 'Failed to cancel leave request', 'error');
            return;
        }

        showToast(`Leave application #${leaveId} has been cancelled.`, 'success');
        await loadEmployeeData();

    } catch (err) {
        console.error(err);
        showToast('Network error while cancelling request', 'error');
    }
}

// ── Modals & Shared Forms ────────────────────────────────────
function openLeaveModal(isAdminMode) {
    if (isAdminMode) {
        elements.leaveEmpSelectWrap.style.display = 'block';
        elements.leaveEmpLockedPill.style.display = 'none';
        populateEmployeeDropdown();
    } else {
        elements.leaveEmpSelectWrap.style.display = 'none';
        elements.leaveEmpLockedPill.style.display = 'flex';
        elements.lockedEmpName.textContent = state.currentUser ? `${state.currentUser.name} (#${state.currentUser.id})` : 'Active Staff';
        elements.leaveEmpId.value = state.currentUser ? state.currentUser.id : '';
    }

    openModal(elements.leaveModal);
}

function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
    
    if (modal === elements.leaveModal) {
        elements.leaveForm.reset();
        clearErrors(['leave-emp-error', 'leave-type-error', 'leave-start-error', 'leave-end-error']);
        elements.calcDaysDisplay.textContent = '0 Days';
    } else if (modal === elements.employeeModal) {
        const form = document.getElementById('modal-employee-form');
        if (form) form.reset();
        clearErrors(['modal-emp-name-error', 'modal-emp-email-error', 'modal-emp-dept-error', 'modal-emp-role-error']);
    }
}

function setupDateValidation() {
    const updateCalculation = () => {
        const startVal = elements.leaveStartDate.value;
        const endVal = elements.leaveEndDate.value;
        const startError = document.getElementById('leave-start-error');
        const endError = document.getElementById('leave-end-error');

        startError.textContent = '';
        endError.textContent = '';

        if (!startVal || !endVal) {
            elements.calcDaysDisplay.textContent = '0 Days';
            return;
        }

        const start = new Date(startVal);
        const end = new Date(endVal);

        if (start > end) {
            endError.textContent = 'Start date cannot be after end date';
            elements.calcDaysDisplay.textContent = 'Invalid Range';
            elements.calcDaysDisplay.style.background = '#fee2e2';
            elements.calcDaysDisplay.style.color = '#dc2626';
            return;
        }

        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

        elements.calcDaysDisplay.textContent = `${diffDays} Day${diffDays > 1 ? 's' : ''}`;
        elements.calcDaysDisplay.style.background = '#ffffff';
        elements.calcDaysDisplay.style.color = '#4338ca';

        elements.calcDaysDisplay.classList.remove('highlight');
        void elements.calcDaysDisplay.offsetWidth;
        elements.calcDaysDisplay.classList.add('highlight');
    };

    elements.leaveStartDate.addEventListener('change', updateCalculation);
    elements.leaveEndDate.addEventListener('change', updateCalculation);
}

// ── Handlers: Leave Application Submission ───────────────────
async function handleLeaveSubmit(e) {
    e.preventDefault();

    let employeeId = elements.leaveEmpId.value;
    if (state.currentUser && state.currentUser.role === 'EMPLOYEE') {
        employeeId = state.currentUser.id;
    }

    const leaveType = elements.leaveType.value;
    const startDate = elements.leaveStartDate.value;
    const endDate = elements.leaveEndDate.value;
    const reason = elements.leaveReason.value.trim();

    clearErrors(['leave-emp-error', 'leave-type-error', 'leave-start-error', 'leave-end-error']);

    let isValid = true;
    if (!employeeId) {
        document.getElementById('leave-emp-error').textContent = 'Employee selection is required';
        isValid = false;
    }
    if (!leaveType) {
        document.getElementById('leave-type-error').textContent = 'Leave type (CL, SL, EL) is required';
        isValid = false;
    }
    if (!startDate) {
        document.getElementById('leave-start-error').textContent = 'Start date is required';
        isValid = false;
    }
    if (!endDate) {
        document.getElementById('leave-end-error').textContent = 'End date is required';
        isValid = false;
    }
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
        document.getElementById('leave-end-error').textContent = 'Start date cannot be after end date';
        isValid = false;
    }

    if (!isValid) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/leaves`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                employeeId: Number(employeeId),
                leaveType,
                startDate,
                endDate,
                reason
            })
        });

        const data = await res.json();

        if (!res.ok) {
            const errorMsg = data.message || (data.errors ? Object.values(data.errors).join(', ') : 'Failed to submit request');
            showToast(errorMsg, 'error');
            return;
        }

        showToast(`Leave application sent successfully (${data.leaveDays} days)!`, 'success');
        closeModal(elements.leaveModal);

        if (state.currentUser.role === 'ADMIN') {
            await loadAdminData();
        } else {
            await loadEmployeeData();
        }

    } catch (err) {
        console.error(err);
        showToast('Network error while submitting leave application', 'error');
    }
}

// ── Handlers: Employee Registration ──────────────────────────
async function handleEmployeeCreate(e, isModal) {
    e.preventDefault();

    const nameId = isModal ? 'modal-emp-name' : 'admin-emp-name';
    const emailId = isModal ? 'modal-emp-email' : 'admin-emp-email';
    const deptId = isModal ? 'modal-emp-dept' : 'admin-emp-dept';
    const roleId = isModal ? 'modal-emp-role' : 'admin-emp-role';

    const nameErrId = isModal ? 'modal-emp-name-error' : 'admin-emp-name-error';
    const emailErrId = isModal ? 'modal-emp-email-error' : 'admin-emp-email-error';
    const deptErrId = isModal ? 'modal-emp-dept-error' : 'admin-emp-dept-error';

    const name = document.getElementById(nameId).value.trim();
    const email = document.getElementById(emailId).value.trim();
    const department = document.getElementById(deptId).value.trim();
    const role = document.getElementById(roleId) ? document.getElementById(roleId).value : 'EMPLOYEE';

    clearErrors([nameErrId, emailErrId, deptErrId]);

    let isValid = true;
    if (!name) {
        document.getElementById(nameErrId).textContent = 'Full name cannot be blank';
        isValid = false;
    }
    if (!email) {
        document.getElementById(emailErrId).textContent = 'Email cannot be blank';
        isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        document.getElementById(emailErrId).textContent = 'Enter a valid email address';
        isValid = false;
    }
    if (!department) {
        document.getElementById(deptErrId).textContent = 'Department is required';
        isValid = false;
    }

    if (!isValid) return;

    try {
        const res = await fetch(`${API_BASE_URL}/api/employees`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, department, role, password: 'password123' })
        });

        const data = await res.json();

        if (!res.ok) {
            const errorMsg = data.message || (data.errors ? Object.values(data.errors).join(', ') : 'Failed to register employee');
            showToast(errorMsg, 'error');
            return;
        }

        showToast(`Account for "${data.name}" (${data.role}) registered!`, 'success');
        
        if (isModal) {
            closeModal(elements.employeeModal);
        } else {
            elements.adminEmployeeForm.reset();
        }

        await loadAdminData();

    } catch (err) {
        console.error(err);
        showToast('Network error while registering staff account', 'error');
    }
}

// ── Dropdowns & UI Utilities ─────────────────────────────────
function populateEmployeeDropdown() {
    const currentVal = elements.leaveEmpId.value;
    elements.leaveEmpId.innerHTML = '<option value="">Select Employee...</option>';

    state.employees.forEach(emp => {
        const opt = document.createElement('option');
        opt.value = emp.id;
        opt.textContent = `${emp.name} (${emp.department}) - #${emp.id}`;
        elements.leaveEmpId.appendChild(opt);
    });

    if (currentVal) elements.leaveEmpId.value = currentVal;
}

function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-circle-check';
    if (type === 'error') icon = 'fa-triangle-exclamation';

    toast.innerHTML = `
        <i class="fa-solid ${icon}"></i>
        <div class="toast-message">${escapeHtml(message)}</div>
    `;

    elements.toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(30px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function clearErrors(ids) {
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = '';
    });
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    const [year, month, day] = dateStr.split('-');
    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    return `${parseInt(day, 10)} ${months[parseInt(month, 10) - 1]} ${year}`;
}

function escapeHtml(text) {
    if (!text) return '';
    const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return String(text).replace(/[&<>"']/g, m => map[m]);
}
