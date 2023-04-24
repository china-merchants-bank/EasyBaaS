export default function access(initialState: { currentUser?: USER.CurrentUser | undefined }) {
  const { currentUser } = initialState || {};
  return {
    isLogin: currentUser && currentUser.token,
  };
}
