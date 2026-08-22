import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext.jsx'
import ProtectedAdminRoute from './auth/ProtectedAdminRoute.jsx'
import Header from './components/Header.jsx'
import PublicSurveyList from './components/PublicSurveyList.jsx'
import PublicSurveyView from './components/PublicSurveyView.jsx'
import AdminLoginPage from './components/AdminLoginPage.jsx'
import AdminSurveyList from './components/AdminSurveyList.jsx'
import AdminSurveyDetail from './components/AdminSurveyDetail.jsx'

// Route map:
//   /                        public survey list (no login)
//   /surveys/:id             public survey view + respond form (no login)
//   /admin/login              admin login page
//   /admin                   admin dashboard - survey list (protected)
//   /admin/surveys/:id       admin survey detail - stats + responses (protected)
function AppShell() {
  return (
    <div className="app-shell">
      <Header />

      <main className="app-main">
        <Routes>
          <Route path="/" element={<PublicSurveyList />} />
          <Route path="/surveys/:id" element={<PublicSurveyView />} />

          <Route path="/admin/login" element={<AdminLoginPage />} />

          <Route element={<ProtectedAdminRoute />}>
            <Route path="/admin" element={<AdminSurveyList />} />
            <Route path="/admin/surveys/:id" element={<AdminSurveyDetail />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      <footer className="app-footer">
        <span className="mono muted">Spring Boot · Spring Security (JWT) · H2 · React · React Router</span>
      </footer>
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppShell />
    </AuthProvider>
  )
}
