export const formatDateTime = (dateTime?: string) => {
  return dateTime ? new Date(dateTime).toLocaleString("zh-CN") : null;
};
