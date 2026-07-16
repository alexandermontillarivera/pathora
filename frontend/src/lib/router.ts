import SchoolDetailPage from "@modules/schools/school-detail-page.svelte"
import ResetPasswordPage from "@modules/auth/reset-password-page.svelte"
import UserProfilePage from "@modules/users/user-profile-page.svelte"
import CommunityPage from "@modules/community/community-page.svelte"
import DiscoverPage from "@modules/discover/discover-page.svelte"
import ActivityPage from "@modules/activity/activity-page.svelte"
import NotFoundPage from "@modules/errors/not-found-page.svelte"
import type { Route } from "@dvcol/svelte-simple-router/models"
import ProfilePage from "@modules/profile/profile-page.svelte"
import SchoolsPage from "@modules/schools/schools-page.svelte"
import PrivacyPage from "@modules/privacy/privacy-page.svelte"
import CareerPage from "@modules/career/career-page.svelte"
import SearchPage from "@modules/search/search-page.svelte"
import { Router } from "@dvcol/svelte-simple-router/router"
import AboutPage from "@modules/about/about-page.svelte"

export const routeNames = {
	schools: "schools",
	home: "home",
	school: "school",
	search: "search",
	community: "community",
	career: "career",
	profile: "profile",
	user: "user",
	resetPassword: "reset-password",
	activity: "activity",
	about: "about",
	privacy: "privacy",
	notFound: "not-found",
} as const
type RouteName = (typeof routeNames)[keyof typeof routeNames]

export const routes: Readonly<Route<RouteName>[]> = [
	{ name: routeNames.home, path: "/", component: DiscoverPage },
	{ name: routeNames.schools, path: "/schools", component: SchoolsPage },
	{
		name: routeNames.school,
		path: "/schools/:id",
		component: SchoolDetailPage,
	},
	{ name: routeNames.search, path: "/search", component: SearchPage },
	{ name: routeNames.community, path: "/community", component: CommunityPage },
	{ name: routeNames.career, path: "/careers/:id", component: CareerPage },
	{ name: routeNames.profile, path: "/profile", component: ProfilePage },
	{ name: routeNames.user, path: "/users/:id", component: UserProfilePage },
	{
		name: routeNames.resetPassword,
		path: "/reset-password",
		component: ResetPasswordPage,
	},
	{ name: routeNames.activity, path: "/activity", component: ActivityPage },
	{ name: routeNames.about, path: "/about", component: AboutPage },
	{ name: routeNames.privacy, path: "/privacy", component: PrivacyPage },
	{ name: routeNames.notFound, path: "*", component: NotFoundPage },
]

export const router = new Router<RouteName>({
	routes,
	hash: false,
	listen: "history",
	// Preserve parameters from direct-entry URLs. Internal navigation cleans
	// parameters from the route being left.
	stripQuery: false,
})

function locationFromHref(href: string) {
	const url = new URL(href, window.location.origin)
	return {
		path: url.pathname,
		query: Object.fromEntries(url.searchParams.entries()),
		stripQuery: true as const,
	}
}

export const navigate = (href: string) => router.push(locationFromHref(href))
export const replaceRoute = (href: string) => router.replace(locationFromHref(href))
